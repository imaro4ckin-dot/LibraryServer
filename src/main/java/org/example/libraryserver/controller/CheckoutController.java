package org.example.libraryserver.controller;

import org.example.libraryserver.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkouts")
public class CheckoutController {

    private final LibraryService service;

    public CheckoutController(LibraryService service) {
        this.service = service;
    }

    /** GET /api/checkouts/active  → map of bookId → username */
    @GetMapping("/active")
    public Map<Integer, String> activeCheckouts() {
        return service.loadActiveCheckouts();
    }

    /** GET /api/checkouts/history */
    @GetMapping("/history")
    public List<Map<String, Object>> history() {
        return service.loadFullHistory();
    }

    /** GET /api/checkouts/user/{userId}/book-ids */
    @GetMapping("/user/{userId}/book-ids")
    public List<Integer> userBookIds(@PathVariable int userId) {
        return service.getCheckedOutBookIds(userId);
    }

    /** GET /api/checkouts/user/{userId}/books */
    @GetMapping("/user/{userId}/books")
    public List<Map<String, Object>> userBooks(@PathVariable int userId) {
        return service.getBooksCheckedOutByUser(userId);
    }

    /** POST /api/checkouts  body: { "bookId": 1, "userId": 2, "loanDays": 14 } */
    @PostMapping
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> body) {
        int bookId   = (int) body.get("bookId");
        int userId   = (int) body.get("userId");
        int loanDays = (int) body.get("loanDays");
        boolean ok = service.checkoutBook(bookId, userId, loanDays);
        return ok ? ResponseEntity.ok(Map.of("success", true))
                  : ResponseEntity.status(409).body(Map.of("error", "Book not available"));
    }

    /** POST /api/checkouts/return  body: { "bookId": 1, "userId": 2 } */
    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnBook(@RequestBody Map<String, Object> body) {
        int bookId = (int) body.get("bookId");
        int userId = (int) body.get("userId");
        boolean ok = service.returnBook(bookId, userId);
        return ok ? ResponseEntity.ok(Map.of("success", true))
                  : ResponseEntity.status(404).body(Map.of("error", "Active checkout not found"));
    }

    /** POST /api/checkouts/admin-return  body: { "bookId": 1 } */
    @PostMapping("/admin-return")
    public ResponseEntity<Void> adminReturn(@RequestBody Map<String, Object> body) {
        service.adminReturnBook((int) body.get("bookId"));
        return ResponseEntity.noContent().build();
    }
}
