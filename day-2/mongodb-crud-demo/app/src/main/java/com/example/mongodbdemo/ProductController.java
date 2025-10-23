package com.example.mongodbdemo;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService svc;

  public ProductController(ProductService svc) {
    this.svc = svc;
  }

  @PostMapping
  public ResponseEntity<Product> create(@Valid @RequestBody Product p) {
    return ResponseEntity.ok(svc.create(p));
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<Product>> bulk(@RequestBody List<@Valid Product> products) {
    return ResponseEntity.ok(svc.bulkCreate(products));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> get(@PathVariable String id) {
    return svc.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/sku/{sku}")
  public ResponseEntity<Product> getBySku(@PathVariable String sku) {
    return svc.getBySku(sku).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public Page<Product> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return svc.search(q, min, max, page, size);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> replace(@PathVariable String id, @Valid @RequestBody Product p) {
    return svc.replace(id, p).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Product> patch(@PathVariable String id, @RequestBody Map<String, Object> fields) {
    return svc.patch(id, fields).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    return svc.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
