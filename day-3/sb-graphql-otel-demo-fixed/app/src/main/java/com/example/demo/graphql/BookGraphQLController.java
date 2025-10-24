package com.example.demo.graphql;

import com.example.demo.Book;
import com.example.demo.BookService;
import com.example.demo.graphql.dto.CreateBookInput;
import com.example.demo.graphql.dto.UpdateBookInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class BookGraphQLController {

    private final BookService service;

    public BookGraphQLController(BookService service) {
        this.service = service;
    }

    @QueryMapping
    public Book bookById(@Argument("id") UUID id) {
        return service.findById(id);
    }

    @QueryMapping
    public List<Book> books() {
        return service.findAll();
    }

    @MutationMapping
    public Book createBook(@Argument("input") CreateBookInput input) {
        return service.create(input.title(), input.author(), input.pages());
    }

    @MutationMapping
    public Book updateBook(@Argument("input") UpdateBookInput input) {
        return service.update(input.id(), input.title(), input.author(), input.pages());
    }

    @MutationMapping
    public boolean deleteBook(@Argument("id") UUID id) {
        return service.delete(id);
    }
}
