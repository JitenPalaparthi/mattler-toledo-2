package com.example.inventory;
import org.springframework.web.bind.annotation.*;
@RestController
class InventoryController {
  @GetMapping("/api/inventory/hello")
  public String hello(){ return "Hello from inventory-service"; }
  @GetMapping("/api/inventory/slow")
  public String slow(@RequestParam(defaultValue="500") long delayMs) throws Exception {
    Thread.sleep(Math.max(0, delayMs));
    return "Inventory slow reply after " + delayMs + " ms";
  }
}
