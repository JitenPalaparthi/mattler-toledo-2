package com.example.inventory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
class InventoryController {
  @GetMapping("/api/inventory/hello")
  public String hello() { return "Hello from inventory-service"; }
}
