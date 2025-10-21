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

# list current topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# create the topic explicitly
docker exec -it kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic orders-topic \
  --partitions 1 \
  --replication-factor 1

# verify it exists now
docker exec -it kafka kafka-topics --describe --bootstrap-server localhost:9092 --topic orders-topic