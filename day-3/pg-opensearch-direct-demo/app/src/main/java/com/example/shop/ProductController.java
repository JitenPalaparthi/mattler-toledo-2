package com.example.shop;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

  private final ProductRepository repo;
  private final ProductService svc;
  private final OpenSearchService os;

  public ProductController(ProductRepository repo, ProductService svc, OpenSearchService os) {
    this.repo = repo;
    this.svc = svc;
    this.os = os;
  }

  @GetMapping("/products")
  public List<Product> all() { return repo.findAll(); }

  @PostMapping(value="/products", consumes=MediaType.APPLICATION_JSON_VALUE)
  public Product create(@RequestBody Product p) { return svc.create(p); }

  @PutMapping(value="/products/{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
  public Product update(@PathVariable Long id, @RequestBody Product patch) { return svc.update(id, patch); }

  @GetMapping("/search")
  public Mono<String> search(@RequestParam(defaultValue="*") String q,
                             @RequestParam(defaultValue="10") int size) {
    return os.search(q, size);
  }
}
