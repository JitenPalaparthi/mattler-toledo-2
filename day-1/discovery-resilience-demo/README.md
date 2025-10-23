# Resilience (Resilience4j) + Eureka Demo
- Spring Boot **3.4.3**
- Spring Cloud **2024.0.2**
- Java **22**

## Modules
- **discovery-server** — Eureka (8761)
- **inventory-service** — test service (8081). Can fail or be slow on demand.
- **order-service** — calls inventory with **Resilience4j** CircuitBreaker + Retry + fallback.

## Build
```bash
mvn -q -DskipTests clean package
```

## Run (local)
```bash
# 1) Eureka
mvn -q -f discovery-server/pom.xml spring-boot:run
# 2) Inventory
mvn -q -f inventory-service/pom.xml spring-boot:run

# 3) Order (has resilience)
mvn -q -f order-service/pom.xml spring-boot:run

```

## Try it

### Healthy path
```bash
curl "http://localhost:8082/api/order/hello"
# -> "Hello from inventory-service"
```

### Force failures (test Retry + CircuitBreaker fallback)
```bash
# inventory returns 500; order-service will retry then use fallback
curl "http://localhost:8082/api/order/hello?fail=true"
# -> "Hello from INVENTORY (FALLBACK)" (after brief retries)
```

### Force slowness (simulate latency)
```bash
# inventory will sleep 2s (default). Adjust with delayMs=3000
curl "http://localhost:8082/api/order/slow"
# -> returns inventory response or fallback if failure threshold reached
```

### Observe circuit state
Open Eureka UI: http://localhost:8761  
Actuator health (order): http://localhost:8082/actuator/health

for i in {1..10}; do curl -s "http://localhost:8082/api/order/hello?fail=true"; echo; done


# healthy check
curl http://localhost:8081/api/inventory/hello

# intentional failure (opens circuit after enough failures)
curl http://localhost:8082/api/order/hello?fail=true

# normal call (works once circuit closes again)
sleep 6
curl http://localhost:8082/api/order/hello

# see order-service health (shows circuit info)
curl http://localhost:8082/actuator/health
