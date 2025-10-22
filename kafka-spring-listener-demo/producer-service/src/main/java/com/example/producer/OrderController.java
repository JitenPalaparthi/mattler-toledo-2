
package com.example.producer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public OrderController(KafkaTemplate<String, Object> kafkaTemplate,
                           @Value("${app.topic.name:orders}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody OrderEvent event) {
        kafkaTemplate.send(topic, event.orderId(), event);
        return ResponseEntity.ok("sent: " + event);
    }
}
