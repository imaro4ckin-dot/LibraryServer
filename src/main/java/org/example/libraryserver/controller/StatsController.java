package org.example.libraryserver.controller;

import org.example.libraryserver.service.LibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final LibraryService service;

    public StatsController(LibraryService service) {
        this.service = service;
    }

    /** GET /api/stats → { "totalBooks": n, "checkedOut": n, "overdue": n } */
    @GetMapping
    public Map<String, Object> stats() {
        return service.getAdminStats();
    }
}
