
package com.example.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Value("${app.topic.name:orders}")
    private String topicName;

    @Value("${app.topic.partitions:1}")
    private int partitions; // default 1 if not provided

    @Value("${app.topic.replicas:1}")
    private short replicas; // default 1 if not provided

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }


}
