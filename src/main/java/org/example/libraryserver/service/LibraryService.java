package org.example.libraryserver.service;

import org.example.libraryserver.model.BookEntity;
import org.example.libraryserver.model.CheckoutEntity;
import org.example.libraryserver.model.UserEntity;
import org.example.libraryserver.repository.BookRepository;
import org.example.libraryserver.repository.CheckoutRepository;
import org.example.libraryserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LibraryService {

    private final BookRepository books;
    private final UserRepository users;
    private final CheckoutRepository checkouts;

    public LibraryService(BookRepository books, UserRepository users, CheckoutRepository checkouts) {
        this.books = books;
        this.users = users;
        this.checkouts = checkouts;
    }

    // -------------------------------------------------------------------------
    // Seed on first run
    // -------------------------------------------------------------------------

    @Transactional
    public void seedIfEmpty() {
        if (users.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole("admin");
            admin.setSecurityQuestion("");
            admin.setSecurityAnswer("");
            users.save(admin);

            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setPassword("user123");
            user.setRole("user");
            user.setSecurityQuestion("");
            user.setSecurityAnswer("");
            users.save(user);
        }
    }

    // -------------------------------------------------------------------------
    // Auth
    // -------------------------------------------------------------------------

    public Optional<UserEntity> login(String username, String password) {
        return users.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    @Transactional
    public boolean changePassword(int userId, String newPassword) {
        return users.findById(userId).map(u -> {
            u.setPassword(newPassword);
            users.save(u);
            return true;
        }).orElse(false);
    }

    public boolean verifyPassword(int userId, String password) {
        return users.findById(userId)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }

    public Optional<String> getSecurityQuestion(String username) {
        return users.findByUsername(username)
                .map(UserEntity::getSecurityQuestion)
                .filter(q -> q != null && !q.isBlank());
    }

    @Transactional
    public boolean resetPasswordIfAnswerCorrect(String username, String answer, String newPassword) {
        Optional<UserEntity> opt = users.findByUsername(username);
        if (opt.isEmpty()) return false;
        UserEntity u = opt.get();
        if (u.getSecurityAnswer() == null || !u.getSecurityAnswer().equalsIgnoreCase(answer.trim()))
            return false;
        u.setPassword(newPassword);
        users.save(u);
        return true;
    }

    // -------------------------------------------------------------------------
    // Books
    // -------------------------------------------------------------------------

    public List<Map<String, Object>> loadBooks() {
        List<CheckoutEntity> active = checkouts.findAllActive();
        Map<Integer, String> activeDueDates = new HashMap<>();
        for (CheckoutEntity c : active) {
            activeDueDates.put(c.getBookId(), c.getDueDate());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (BookEntity b : books.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", b.getId());
            m.put("title", b.getTitle());
            m.put("author", b.getAuthor());
            m.put("available", b.getAvailable() == 1);
            m.put("dueDate", activeDueDates.getOrDefault(b.getId(), ""));
            result.add(m);
        }
        return result;
    }

    @Transactional
    public int addBook(String title, String author) {
        BookEntity b = new BookEntity(title, author);
        return books.save(b).getId();
    }

    @Transactional
    public boolean updateBook(int id, String title, String author) {
        return books.findById(id).map(b -> {
            b.setTitle(title);
            b.setAuthor(author);
            books.save(b);
            return true;
        }).orElse(false);
    }

    @Transactional
    public void deleteBook(int id) {
        checkouts.deleteByBookId(id);
        books.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Checkouts
    // -------------------------------------------------------------------------

    @Transactional
    public boolean checkoutBook(int bookId, int userId, int loanDays) {
        Optional<BookEntity> opt = books.findById(bookId);
        if (opt.isEmpty() || opt.get().getAvailable() == 0) return false;
        BookEntity book = opt.get();
        book.setAvailable(0);
        books.save(book);

        CheckoutEntity c = new CheckoutEntity();
        c.setBookId(bookId);
        c.setUserId(userId);
        c.setCheckedOutAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        c.setDueDate(LocalDate.now().plusDays(loanDays).toString());
        checkouts.save(c);
        return true;
    }

    @Transactional
    public boolean returnBook(int bookId, int userId) {
        Optional<CheckoutEntity> opt = checkouts.findByBookIdAndUserIdAndReturnedAtIsNull(bookId, userId);
        if (opt.isEmpty()) return false;
        CheckoutEntity c = opt.get();
        c.setReturnedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        checkouts.save(c);
        books.findById(bookId).ifPresent(b -> { b.setAvailable(1); books.save(b); });
        return true;
    }

    @Transactional
    public void adminReturnBook(int bookId) {
        checkouts.findByBookIdAndReturnedAtIsNull(bookId).ifPresent(c -> {
            c.setReturnedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            checkouts.save(c);
        });
        books.findById(bookId).ifPresent(b -> { b.setAvailable(1); books.save(b); });
    }

    public Map<Integer, String> loadActiveCheckouts() {
        Map<Integer, String> map = new HashMap<>();
        for (CheckoutEntity c : checkouts.findAllActive()) {
            users.findById(c.getUserId()).ifPresent(u -> map.put(c.getBookId(), u.getUsername()));
        }
        return map;
    }

    public List<Integer> getCheckedOutBookIds(int userId) {
        return checkouts.findByUserIdAndReturnedAtIsNull(userId)
                .stream().map(CheckoutEntity::getBookId).toList();
    }

    public List<Map<String, Object>> loadFullHistory() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (CheckoutEntity c : checkouts.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "checkedOutAt"))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            String bookTitle = books.findById(c.getBookId()).map(BookEntity::getTitle).orElse("?");
            String username  = users.findById(c.getUserId()).map(UserEntity::getUsername).orElse("?");
            m.put("bookTitle",    bookTitle);
            m.put("username",     username);
            m.put("checkedOutAt", c.getCheckedOutAt());
            m.put("dueDate",      c.getDueDate());
            m.put("returnedAt",   c.getReturnedAt() != null ? c.getReturnedAt() : "—");
            result.add(m);
        }
        return result;
    }

    public List<Map<String, Object>> getBooksCheckedOutByUser(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (CheckoutEntity c : checkouts.findByUserIdAndReturnedAtIsNull(userId)) {
            books.findById(c.getBookId()).ifPresent(b -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",      b.getId());
                m.put("title",   b.getTitle());
                m.put("author",  b.getAuthor());
                m.put("dueDate", c.getDueDate());
                result.add(m);
            });
        }
        result.sort(Comparator.comparing(m -> (String) m.get("dueDate")));
        return result;
    }

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    public List<Map<String, Object>> loadUsers() {
        return users.findAll(org.springframework.data.domain.Sort.by("id"))
                .stream()
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id",       u.getId());
                    m.put("username", u.getUsername());
                    m.put("role",     u.getRole());
                    return m;
                }).toList();
    }

    @Transactional
    public int addUser(String username, String password, String role,
                       String securityQuestion, String securityAnswer) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        u.setSecurityQuestion(securityQuestion == null ? "" : securityQuestion);
        u.setSecurityAnswer(securityAnswer == null ? "" : securityAnswer.toLowerCase().trim());
        return users.save(u).getId();
    }

    @Transactional
    public void deleteUser(int userId) {
        checkouts.deleteByUserId(userId);
        users.deleteById(userId);
    }

    public boolean usernameExists(String username) {
        return users.existsByUsername(username);
    }
}
