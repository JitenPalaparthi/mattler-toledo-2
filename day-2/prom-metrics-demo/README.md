# Spring Boot → Prometheus Metrics Demo (with Custom Metrics)

This is a minimal, production-style demo that exposes **/actuator/prometheus** and publishes **custom metrics** (Counter, Timer, Gauge, DistributionSummary, LongTaskTimer). It also ships with **Prometheus** via docker-compose.

## Stack
- Java 22, Spring Boot 3.3.x
- Spring Actuator + Micrometer + Prometheus registry
- Docker (multi-stage build) + docker-compose
- Prometheus 2.55.x

## Run (one command)
```bash
docker compose up -d --build
```

Open:
- App health: http://localhost:8080/api/healthz
- Prometheus: http://localhost:9090  (query for `orders_created_total`, `business_timer_seconds_*`, `queue_depth`, `payload_bytes_*` etc.)

## Generate some metrics
```bash
# One-off
curl -s -X POST localhost:8080/api/order -H 'Content-Type: application/json' -d '{"orderId":"1","item":"widget"}' | jq .

# Burst of requests
for i in $(seq 1 50); do
  curl -s -X POST localhost:8080/api/order -H 'Content-Type: application/json' -d "{"orderId":"$i","item":"gadget"}" > /dev/null
done
```

## Custom metrics implemented
- **Counter**: `orders_created_total`
- **Timer**: `business.timer` (percentiles + histogram enabled)
- **Gauge**: `queue.depth` (tracks a simulated in-memory queue length)
- **DistributionSummary**: `payload.bytes` (payload size histogram)
- **LongTaskTimer**: `batch.longtask` with `/api/batch/start` and `/api/batch/stop/{id}`

## Key files
- `app/src/main/java/com/example/metricsdemo/DemoController.java` → all custom metrics usage
- `app/src/main/resources/application.yml` → actuator exposure + histogram/percentile config
- `prometheus.yml` → scrape config for `/actuator/prometheus`
- `docker-compose.yml` → brings up app + Prometheus
- `Dockerfile` → multi-stage build, Java 22

## Notes
- Actuator publishes a lot of default metrics (JVM, CPU, HTTP server). We explicitly add a few JVM binders as well; you can remove them if you want to rely solely on Actuator.
- To add **exemplars** with trace IDs, wire in Spring Observability / OTel; Micrometer will surface exemplars on supported backends.

## Cleanup
```bash
docker compose down -v
```

## Extend
- Add Grafana service and dashboards.
- Add Micrometer `@Timed` on controller/service methods.
- Add `MeterFilter` to set common tags or limit high-cardinality labels.
