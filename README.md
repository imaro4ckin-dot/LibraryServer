# Library Server

Spring Boot REST API backend for the Library Management System.

## Requirements

- Java 21+

## Run

**Mac / Linux**
```bash
./mvnw spring-boot:run
```

**Windows**
```cmd
mvnw.cmd spring-boot:run
```

Starts on **port 8080**. Creates `library.db` (SQLite) in the working directory on first launch and seeds the two default accounts.

## API overview

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Authenticate a user |
| POST | `/api/auth/change-password` | Change password (verifies current first) |
| GET  | `/api/auth/security-question?username=` | Fetch security question |
| POST | `/api/auth/reset-password` | Reset password via security answer |
| GET  | `/api/books` | List all books with availability |
| POST | `/api/books` | Add a book |
| PUT  | `/api/books/{id}` | Edit a book |
| DELETE | `/api/books/{id}` | Delete a book |
| POST | `/api/checkouts` | Checkout a book |
| POST | `/api/checkouts/return` | Return a book (user) |
| POST | `/api/checkouts/admin-return` | Force-return a book (admin) |
| GET  | `/api/checkouts/active` | Map of bookId → username for active loans |
| GET  | `/api/checkouts/history` | Full checkout history |
| GET  | `/api/checkouts/user/{id}/books` | Books currently checked out by a user |
| GET  | `/api/users` | List users |
| POST | `/api/users` | Create a user |
| DELETE | `/api/users/{id}` | Delete a user |
| GET  | `/api/users/exists?username=` | Check if username is taken |
