package com.example.shop;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductRepository repo;
    private final WebClient web;
    private final ObjectMapper mapper;

    @Value("${opensearch.url}")
    private String osUrl;

    @Value("${opensearch.index:products}")
    private String index;

    public ProductController(ProductRepository repo, WebClient.Builder builder, ObjectMapper mapper) {
        this.repo = repo;
        this.web = builder.build();
        this.mapper = mapper;
    }

    // --- Postgres (for comparison) ---
    @GetMapping("/products")
    public List<Product> all() {
        return repo.findAll();
    }

    // --- Search from OpenSearch ---
    @GetMapping("/search")
    public Mono<String> search(@RequestParam(defaultValue = "*") String q,
                               @RequestParam(defaultValue = "10") int size) {
        String url = osUrl + "/" + index + "/_search";

        Map<String, Object> payload = Map.of(
            "query", Map.of("query_string", Map.of("query", q)),
            "size", size
        );

        return web.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload) // Jackson encodes it; no fragile string escaping
            .retrieve()
            .bodyToMono(String.class);
    }

    // Raw OpenSearch doc by id
    @GetMapping("/doc/{id}")
    public Mono<String> doc(@PathVariable String id) {
        String url = osUrl + "/" + index + "/_doc/" + id;
        return web.get().uri(url).retrieve().bodyToMono(String.class);
    }

    // Count in OpenSearch
    @GetMapping("/count")
    public Mono<String> count() {
        String url = osUrl + "/" + index + "/_count";
        return web.get().uri(url).retrieve().bodyToMono(String.class);
    }
}