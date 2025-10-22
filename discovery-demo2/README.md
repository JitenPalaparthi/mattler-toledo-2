# Discovery Demo (Spring Boot 3.4.3, Spring Cloud 2024.0.2, Java 22)

Services:
- `discovery-server` (Eureka Server, port 8761)
- `inventory-service` (Eureka Client, port 8081)
- `order-service` (Eureka Client, port 8082) — calls `inventory-service` via service discovery

## Prereqs
- Java 22 (or 21) and Maven 3.9+
- Docker & Docker Compose (optional for container run)

## Build (Maven)
```bash
mvn -q -DskipTests package
```

## Run locally (each terminal)

**1) Start Eureka**
```bash
# mvn -q -pl discovery-server -am spring-boot:run
mvn -q -f discovery-server/pom.xml spring-boot:run
# UI: http://localhost:8761
```

**2) Start inventory-service**
```bash
#mvn -q -pl inventory-service -am spring-boot:run
mvn -q -f inventory-service/pom.xml spring-boot:run
```

**3) Start order-service**
```bash
#mvn -q -pl order-service -am spring-boot:run
mvn -q -f order-service/pom.xml spring-boot:run
```

### Test
```bash
# Service->Service call via discovery + LoadBalancer
curl http://localhost:8082/api/order/call-inventory
# => "Hello from inventory-service"
```

## Run with Docker Compose
```bash
docker compose up --build
# Test after containers are healthy:
curl http://localhost:8082/api/order/call-inventory
```

## Notes
- Discovery clients use `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` env var in Compose.
- For production, add health/metrics, retries (Resilience4j), and proper logging.
