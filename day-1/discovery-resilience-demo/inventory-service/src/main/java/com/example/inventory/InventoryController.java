package com.example.inventory;
import org.springframework.web.bind.annotation.*;
@RestController
class InventoryController {
  @GetMapping("/api/inventory/hello")
  public String hello(@RequestParam(name="fail", required=false, defaultValue="false") boolean fail) {
    if (fail) { throw new RuntimeException("Forced failure from inventory-service"); }
    return "Hello from inventory-service";
  }
  @GetMapping("/api/inventory/slow")
  public String slow(@RequestParam(name="delayMs", required=false, defaultValue="2000") long delayMs) throws InterruptedException {
    Thread.sleep(Math.max(0, delayMs));
    return "Inventory responded after " + delayMs + " ms";
  }
}
