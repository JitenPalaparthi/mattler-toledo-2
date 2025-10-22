
package com.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrdersListener {

    private static final Logger log = LoggerFactory.getLogger(OrdersListener.class);

    @KafkaListener(
            topics = "${app.topic.name:orders}",
            groupId = "${app.consumer.group:inventory-consumer}",
            properties = {
                "spring.json.value.default.type=com.example.consumer.OrderEvent"
            }
    )
    public void listen(@Payload OrderEvent event, ConsumerRecord<String, OrderEvent> record) {
        log.info("📥 Consumed key={} partition={} offset={} value={}", record.key(), record.partition(), record.offset(), event);
    }
}
