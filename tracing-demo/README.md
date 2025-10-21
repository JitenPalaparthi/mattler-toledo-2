# Distributed Tracing Demo (Micrometer + OpenTelemetry)
Stack: Spring Boot 3.4.3, Spring Cloud 2024.0.2, Java 22, Eureka, Spring Cloud Gateway, OTel Collector, Jaeger

Modules:
- discovery-server (8761)
- api-gateway (8080)
- order-service (8082)
- inventory-service (8081)
- otel-collector + jaeger via docker-compose

Build:
- mvn -q -DskipTests clean package

Run (Docker):
- docker compose up --build
- Open Jaeger: http://localhost:16686
- Try:
  curl http://localhost:8080/api/order/call-inventory
  curl http://localhost:8080/api/inventory/slow?delayMs=750
