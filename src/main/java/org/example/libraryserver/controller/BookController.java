package org.example.libraryserver.controller;

import org.example.libraryserver.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryService service;

    public BookController(LibraryService service) {
        this.service = service;
    }

    /** GET /api/books */
    @GetMapping
    public List<Map<String, Object>> listBooks() {
        return service.loadBooks();
    }

    /** POST /api/books  body: { "title": "...", "author": "..." } */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addBook(@RequestBody Map<String, String> body) {
        String title  = body.get("title");
        String author = body.get("author");
        if (title == null || title.isBlank() || author == null || author.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "title and author required"));
        int id = service.addBook(title, author);
        return ResponseEntity.ok(Map.of("id", id));
    }

    /** PUT /api/books/{id}  body: { "title": "...", "author": "..." } */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable int id,
                                                          @RequestBody Map<String, String> body) {
        boolean ok = service.updateBook(id, body.get("title"), body.get("author"));
        return ok ? ResponseEntity.ok(Map.of("success", true))
                  : ResponseEntity.status(404).body(Map.of("error", "Book not found"));
    }

    /** DELETE /api/books/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
