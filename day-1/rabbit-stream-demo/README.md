# Spring Cloud Stream Kafka Demo

## Services
- **order-producer** – Sends `OrderCreated` messages to Kafka topic `orders-topic`
- **inventory-consumer** – Consumes messages from `orders-topic` and logs them
- **Kafka + Zookeeper** – Using Bitnami images

### Run
```bash
docker compose up -d
mvn -q -f order-producer/pom.xml spring-boot:run
mvn -q -f inventory-consumer/pom.xml spring-boot:run
```

You should see messages printed in both services' logs.

2) How RabbitMQ is different from Kafka (quick, practical)