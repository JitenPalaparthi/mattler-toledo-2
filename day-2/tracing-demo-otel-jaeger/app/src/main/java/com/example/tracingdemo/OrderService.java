package com.example.tracingdemo;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class OrderService {
  private final OrderRepository repo;
  public OrderService(OrderRepository repo) { this.repo = repo; }
  @Observed(name="svc.order.getById", lowCardinalityKeyValues={"op","getById"})
  public Optional<Order> get(long id) { return repo.findById(id); }
  @Observed(name="svc.order.list", lowCardinalityKeyValues={"op","list"})
  public List<Order> list() { return repo.findAll(); }
  @Observed(name="svc.order.create", lowCardinalityKeyValues={"op","create"})
  public long create(String customer, String sku, int qty, int priceCents) {
    return repo.create(customer, sku, qty, priceCents);
  }
}
