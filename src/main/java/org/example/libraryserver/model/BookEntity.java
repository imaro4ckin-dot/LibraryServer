package org.example.libraryserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int available = 1;

    public BookEntity() {}

    public BookEntity(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = 1;
    }

    public int getId()             { return id; }
    public void setId(int id)      { this.id = id; }
    public String getTitle()       { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getAuthor()      { return author; }
    public void setAuthor(String a){ this.author = a; }
    public int getAvailable()      { return available; }
    public void setAvailable(int a){ this.available = a; }
}
