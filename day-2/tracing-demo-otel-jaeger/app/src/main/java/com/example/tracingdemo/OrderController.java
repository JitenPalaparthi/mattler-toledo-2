package com.example.tracingdemo;
import io.micrometer.observation.annotation.Observed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService svc;
  public OrderController(OrderService svc) { this.svc = svc; }
  @GetMapping("/{id}")
  @Observed(name="http.order.getById", lowCardinalityKeyValues={"http","GET /api/orders/{id}"})
  public ResponseEntity<?> get(@PathVariable long id) {
    return svc.get(id).<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
  @GetMapping
  @Observed(name="http.order.list", lowCardinalityKeyValues={"http","GET /api/orders"})
  public List<Order> list() { return svc.list(); }
  @PostMapping
  @Observed(name="http.order.create", lowCardinalityKeyValues={"http","POST /api/orders"})
  public Map<String,Object> create(@RequestBody Map<String,Object> body) {
    String customer = (String) body.getOrDefault("customer","Anonymous");
    String sku = (String) body.getOrDefault("sku","sku-1");
    int qty = ((Number) body.getOrDefault("qty",1)).intValue();
    int priceCents = ((Number) body.getOrDefault("priceCents",999)).intValue();
    long id = svc.create(customer, sku, qty, priceCents);
    return Map.of("id", id, "status", "created");
  }
}
