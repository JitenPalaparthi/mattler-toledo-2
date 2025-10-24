# Spring Boot → OTel Collector → Prometheus (+ Grafana)

This demo shows a Spring Boot 3.3 app exporting **metrics via OTLP** to the **OpenTelemetry Collector**, which exposes **Prometheus-format** metrics for Prometheus to scrape. Grafana is included for quick visualization.

## Stack
- Spring Boot (Actuator + Micrometer OTLP)
- OTel Collector (Prometheus exporter)
- Prometheus
- Grafana

## Run
```bash
docker compose up --build
```
- App: http://localhost:8080/hello
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000  (admin / admin)
- Collector metrics endpoint: http://localhost:9464/metrics

Hit the endpoint a few times:
```bash
curl "http://localhost:8080/hello?name=Jiten"
```

Then open Prometheus and query (examples):
- `demo_hello_requests_total`
- `rate(demo_hello_requests_total[1m])`
- `http_server_requests_seconds_count`
- `http_server_requests_seconds_sum`

## Notes
- The app sends metrics to the collector via **OTLP/HTTP** `:4318` (see `application.properties`).
- Collector converts OTLP → Prometheus and serves at `:9464`. Prometheus scrapes that endpoint (`prometheus.yml`).
- You can also enable **remote-write** from the Collector to Prometheus if preferred.
