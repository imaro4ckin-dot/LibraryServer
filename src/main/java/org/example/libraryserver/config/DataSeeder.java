package org.example.libraryserver.config;

import org.example.libraryserver.service.LibraryService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner seedDefaults(LibraryService service) {
        return args -> service.seedIfEmpty();
    }
}
