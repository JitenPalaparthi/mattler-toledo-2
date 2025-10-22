
# Kafka Spring Boot Demo — Producer & Consumer (@KafkaListener)

This is a minimal end‑to‑end demo with:
- **Kafka (KRaft, single-node)** via Docker Compose
- **producer-service** (Spring Boot 3, Java 22) — exposes `POST /api/orders` to publish to Kafka
- **consumer-service** (Spring Boot 3, Java 22) — uses `@KafkaListener` to consume from Kafka

## Quick start

```bash
# 1) Build JARs
./mvnw -v || echo "If Maven Wrapper isn't available on your system, install Maven 3.9+"
( cd producer-service && ./mvnw -q -DskipTests package || mvn -q -DskipTests package )
( cd consumer-service && ./mvnw -q -DskipTests package || mvn -q -DskipTests package )

# 2) Build images & run everything
docker compose up -d --build

# 3) Produce a message
curl -X POST http://localhost:8088/api/orders \
  -H 'Content-Type: application/json' \  -d '{"orderId":"o-101","item":"laptop"}'

# 4) Watch consumer logs
docker logs -f consumer-service
```

## Services / Ports

- Kafka broker: **9092** (internal DNS `kafka:9092`)
- Producer REST: **8088** → `POST /api/orders` (JSON: `{ "orderId": "...", "item": "..." }`)
- Consumer: no external port (logs only)

## Reset

```bash
docker compose down -v
```

## Notes
- Both apps use Spring Boot 3.3.x & Java 22 (Temurin). Adjust `pom.xml` if needed.
- Topic is auto-created (`orders`, 1 partition) by broker config in Compose.
