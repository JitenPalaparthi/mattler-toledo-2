package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@RestController
public class DemoApplication {

  private final Counter helloCounter;

  public DemoApplication(MeterRegistry registry) {
    this.helloCounter = Counter.builder("demo_hello_requests_total")
        .description("How many times /hello was called")
        .register(registry);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(defaultValue = "world") String name) {
    helloCounter.increment();
    return "Hello, " + name + "!";
  }

  // CPU burn for ~N ms to make CPU metrics/GC visible
  @GetMapping("/cpu")
  public String cpu(@RequestParam(defaultValue = "500") long ms) {
    long end = System.currentTimeMillis() + Math.max(0, ms);
    double x = 0;
    while (System.currentTimeMillis() < end) {
      x += Math.sqrt(ThreadLocalRandom.current().nextDouble(1, 1000));
    }
    return "CPU busy for ~" + ms + " ms. x=" + x;
  }

  // Allocate ~sizeMB to trigger GC; memory is released after method returns
  @GetMapping("/alloc")
  public String alloc(@RequestParam(defaultValue = "10") int sizeMB) {
    int n = Math.max(1, sizeMB);
    List<byte[]> blocks = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      blocks.add(new byte[1024 * 1024]); // 1MB
    }
    return "Allocated ~" + n + " MB (temporary).";
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}
