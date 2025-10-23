package com.example.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
class OrderController {
  private final RestTemplate rt;
  OrderController(RestTemplate rt){ this.rt = rt; }

  @GetMapping("/api/order/call-inventory")
  public String callInventory() {
    return rt.getForObject("http://inventory-service:8081/api/inventory/hello", String.class);
  }
}
