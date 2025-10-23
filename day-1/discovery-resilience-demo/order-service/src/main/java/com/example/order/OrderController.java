package com.example.order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
class OrderController {
  private final RestTemplate rt;
  OrderController(RestTemplate rt){ this.rt = rt; }

  @GetMapping("/api/order/hello")
  @Retry(name="inventory", fallbackMethod="helloFallback")
  @CircuitBreaker(name="inventory", fallbackMethod="helloFallback")
  public String hello(@RequestParam(name="fail", required=false, defaultValue="false") boolean fail){
    String url = "http://inventory-service/api/inventory/hello" + (fail ? "?fail=true" : "");
    return rt.getForObject(url, String.class);
  }

  @GetMapping("/api/order/slow")
  @Retry(name="inventory", fallbackMethod="helloFallback")
  @CircuitBreaker(name="inventory", fallbackMethod="helloFallback")
  public String slow(@RequestParam(name="delayMs", required=false, defaultValue="2000") long delayMs){
    String url = "http://inventory-service/api/inventory/slow?delayMs=" + delayMs;
    return rt.getForObject(url, String.class);
  }

  // Fallback must match signature + Throwable tail parameter
  private String helloFallback(boolean fail, Throwable t){
    return "Hello from INVENTORY (FALLBACK) - reason: " + t.getClass().getSimpleName();
  }
  private String helloFallback(long delayMs, Throwable t){
    return "Inventory SLOW (FALLBACK) - reason: " + t.getClass().getSimpleName();
  }
}
