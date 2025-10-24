package com.example.demo.graphql.dto;

import java.util.UUID;

public record UpdateBookInput(UUID id, String title, String author, Integer pages) { }
