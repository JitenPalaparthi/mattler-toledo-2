package com.example.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.cloud.stream.function.StreamBridge;

@SpringBootApplication
@EnableScheduling
public class OrderProducerApp {

  private final StreamBridge bridge;
  public OrderProducerApp(StreamBridge bridge) { this.bridge = bridge; }

  public static void main(String[] args) {
    SpringApplication.run(OrderProducerApp.class, args);
  }

  @Scheduled(fixedRate = 5000)
  void sendOrder() {
    var order = new OrderCreated("ORD-" + System.currentTimeMillis(), "Laptop");
    bridge.send("orders-out-0", MessageBuilder.withPayload(order).build());
    System.out.println("✅ Sent: " + order);
  }

  public record OrderCreated(String orderId, String item) {}
}
