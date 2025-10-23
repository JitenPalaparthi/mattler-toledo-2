# Tracing Demo (FIXED): Spring Boot + Postgres + OpenTelemetry + Jaeger

Run:
  docker compose up -d --build
Open:
  API     -> http://localhost:8080/api/orders
  Jaeger  -> http://localhost:16686 (Service: tracing-demo)
Generate traces:
  curl http://localhost:8080/api/orders
  curl http://localhost:8080/api/orders/1
  curl -X POST http://localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"customer":"Jiten","sku":"sku-9","qty":2,"priceCents":3999}'
