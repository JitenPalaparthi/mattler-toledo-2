package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Consumer;

@SpringBootApplication
public class InventoryConsumerApp {
  public static void main(String[] args) {
    SpringApplication.run(InventoryConsumerApp.class, args);
  }

  @Bean
  public Consumer<OrderCreated> orders() {
    return order -> System.out.println("📦 Received: " + order);
  }

  public record OrderCreated(String orderId, String item) {}
}
