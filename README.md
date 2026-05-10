# Library Server

Spring Boot REST API backend for the Library Management System. Stores all data in a local SQLite database. The [Library Client](https://github.com/YOUR_USERNAME/LibraryClient) connects to this server over HTTP.

## Requirements

- Java 21+

## Run

```bash
# Mac / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

No installation needed — Maven downloads all dependencies automatically.

The server starts on **port 8080** and creates `library.db` in the working directory on first launch, seeding two default accounts:

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | Admin |
| user     | user123   | User  |

## Multi-machine setup

Run this server on one machine. Then open `ApiClient.java` in the client project and set `BASE_URL` to this machine's local IP address (e.g. `http://192.168.1.10:8080`). Any number of laptops can then run the client and share the same data.

## API reference

### Auth

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/login` | Authenticate — body: `{ username, password }` |
| `POST` | `/api/auth/change-password` | Change password — body: `{ userId, currentPassword, newPassword }` |
| `GET`  | `/api/auth/security-question?username=` | Get security question for a user |
| `POST` | `/api/auth/reset-password` | Reset password via security answer — body: `{ username, answer, newPassword }` |

### Books

| Method | Path | Description |
|--------|------|-------------|
| `GET`    | `/api/books` | List all books with availability, ISBN, category, and due date |
| `POST`   | `/api/books` | Add a book — body: `{ title, author, isbn, category }` |
| `PUT`    | `/api/books/{id}` | Edit a book — body: `{ title, author, isbn, category }` |
| `DELETE` | `/api/books/{id}` | Delete a book (also removes its checkout history) |

### Checkouts

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/checkouts` | Check out a book — body: `{ bookId, userId, loanDays }` |
| `POST` | `/api/checkouts/return` | Return a book — body: `{ bookId, userId }` |
| `POST` | `/api/checkouts/admin-return` | Force-return a book — body: `{ bookId }` |
| `GET`  | `/api/checkouts/active` | Map of `bookId → username` for all active loans |
| `GET`  | `/api/checkouts/history` | Full checkout history (all time) |
| `GET`  | `/api/checkouts/user/{id}/books` | Books currently checked out by a user |
| `GET`  | `/api/checkouts/user/{id}/book-ids` | IDs of books currently checked out by a user |

### Users

| Method | Path | Description |
|--------|------|-------------|
| `GET`    | `/api/users` | List all users |
| `POST`   | `/api/users` | Create a user — body: `{ username, password, role, securityQuestion, securityAnswer }` |
| `DELETE` | `/api/users/{id}` | Delete a user (also removes their checkout history) |
| `GET`    | `/api/users/exists?username=` | Check if a username is already taken |

### Stats

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/stats` | Admin dashboard numbers — returns `{ totalBooks, checkedOut, overdue }` |
