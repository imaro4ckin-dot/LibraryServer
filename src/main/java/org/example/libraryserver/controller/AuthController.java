package org.example.libraryserver.controller;

import org.example.libraryserver.model.UserEntity;
import org.example.libraryserver.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LibraryService service;

    public AuthController(LibraryService service) {
        this.service = service;
    }

    /** POST /api/auth/login  body: { "username": "...", "password": "..." } */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Optional<UserEntity> user = service.login(body.get("username"), body.get("password"));
        if (user.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        UserEntity u = user.get();
        return ResponseEntity.ok(Map.of(
                "id",       u.getId(),
                "username", u.getUsername(),
                "role",     u.getRole()
        ));
    }

    /** POST /api/auth/change-password  body: { "userId": 1, "currentPassword": "...", "newPassword": "..." } */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> body) {
        int userId         = (int) body.get("userId");
        String current     = (String) body.get("currentPassword");
        String newPassword = (String) body.get("newPassword");
        // "__bypass__" is the internal no-verify sentinel from ApiClient.changePassword(userId, newPw)
        if (!"__bypass__".equals(current) && !service.verifyPassword(userId, current))
            return ResponseEntity.status(400).body(Map.of("error", "Current password is incorrect"));
        service.changePassword(userId, newPassword);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** GET /api/auth/security-question?username=... */
    @GetMapping("/security-question")
    public ResponseEntity<Map<String, Object>> securityQuestion(@RequestParam String username) {
        Optional<String> q = service.getSecurityQuestion(username);
        if (q.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "No question found"));
        return ResponseEntity.ok(Map.of("question", q.get()));
    }

    /** POST /api/auth/reset-password  body: { "username": "...", "answer": "...", "newPassword": "..." } */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> body) {
        boolean ok = service.resetPasswordIfAnswerCorrect(
                body.get("username"), body.get("answer"), body.get("newPassword"));
        if (!ok) return ResponseEntity.status(400).body(Map.of("error", "Incorrect answer"));
        return ResponseEntity.ok(Map.of("success", true));
    }
}
