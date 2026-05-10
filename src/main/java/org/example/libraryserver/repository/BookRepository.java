package org.example.libraryserver.repository;

import org.example.libraryserver.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {}
