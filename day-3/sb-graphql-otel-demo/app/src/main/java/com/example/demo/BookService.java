package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository repo;
    private final Counter createdCounter;

    public BookService(BookRepository repo, MeterRegistry registry) {
        this.repo = repo;
        this.createdCounter = Counter.builder("app_books_created_total")
                .description("Total books created via GraphQL mutation")
                .register(registry);
    }

    public List<Book> findAll() {
        return repo.findAll();
    }

    public Book findById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional
    public Book create(String title, String author, int pages) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setPages(pages);
        Book saved = repo.save(b);
        createdCounter.increment();
        return saved;
    }

    @Transactional
    public Book update(UUID id, String title, String author, Integer pages) {
        Book b = repo.findById(id).orElseThrow();
        if (title != null) b.setTitle(title);
        if (author != null) b.setAuthor(author);
        if (pages != null) b.setPages(pages);
        return repo.save(b);
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
