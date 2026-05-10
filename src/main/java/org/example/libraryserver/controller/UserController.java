package org.example.libraryserver.controller;

import org.example.libraryserver.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final LibraryService service;

    public UserController(LibraryService service) {
        this.service = service;
    }

    /** GET /api/users */
    @GetMapping
    public List<Map<String, Object>> listUsers() {
        return service.loadUsers();
    }

    /** POST /api/users  body: { "username": "...", "password": "...", "role": "...",
     *                           "securityQuestion": "...", "securityAnswer": "..." } */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "username required"));
        if (service.usernameExists(username))
            return ResponseEntity.status(409).body(Map.of("error", "Username already taken"));
        int id = service.addUser(
                username,
                body.getOrDefault("password", ""),
                body.getOrDefault("role", "user"),
                body.getOrDefault("securityQuestion", ""),
                body.getOrDefault("securityAnswer", "")
        );
        return ResponseEntity.ok(Map.of("id", id));
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/users/exists?username=... */
    @GetMapping("/exists")
    public Map<String, Object> exists(@RequestParam String username) {
        return Map.of("exists", service.usernameExists(username));
    }
}
