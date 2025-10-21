# API Gateway + Service Discovery Demo
Versions: Spring Boot 3.4.3, Spring Cloud 2024.0.2, Java 22

Modules:
- discovery-server (Eureka, 8761)
- inventory-service (8081)
- order-service (8082)
- api-gateway (Spring Cloud Gateway, 8080)

## Build
mvn -q -DskipTests clean package

## Run (local)
# 1) Eureka
mvn -q -pl discovery-server -am spring-boot:run
# 2) Inventory
mvn -q -pl inventory-service -am spring-boot:run
# 3) Order
mvn -q -pl order-service -am spring-boot:run
# 4) Gateway
mvn -q -pl api-gateway -am spring-boot:run

## Test through Gateway
curl http://localhost:8080/api/inventory/hello
curl http://localhost:8080/api/order/call-inventory

## Docker Compose
docker compose up --build
