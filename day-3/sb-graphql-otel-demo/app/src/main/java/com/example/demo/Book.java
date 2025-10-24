package com.example.demo;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Book {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String author;
    private int pages;
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
