# Postgres → Debezium → Kafka → OpenSearch demo (+ Spring REST)

This stack seeds **100 products** into Postgres, streams changes via **Debezium** into Kafka, and sinks them into **OpenSearch**. The **Spring Boot** app exposes REST endpoints and can query OpenSearch.

## Run
```bash
docker compose up --build
```

Wait for services (Kafka Connect downloads plugins on first run).

## Connectors (auto-created)
- **Debezium PG Source**: streams `public.products` to topic `shopserver.public.products`
- **OpenSearch Sink**: consumes the topic and upserts into index `products`

You can verify in Kafka Connect UI (REST):
```
curl http://localhost:8083/connectors | jq .
```

## OpenSearch
- API: http://localhost:9200
- Dashboards: http://localhost:5601

## Spring app
- API base: http://localhost:8080/api
- List from Postgres: `GET /api/products`
- Search OpenSearch: `GET /api/search?q=Product AND category:Books&size=5`
- Count in OpenSearch: `GET /api/count`

## Expected flow
1. App starts and seeds 100 rows into **Postgres**.
2. **Debezium** snapshots the `products` table then streams CDC.
3. **Kafka Connect OpenSearch sink** writes docs into index `products`.
4. Use `GET /api/search` to fetch from OpenSearch.

## Troubleshooting
- Check Connect logs:
  ```bash
  docker logs connect --tail=200
  ```
- Check connector configs:
  ```bash
  curl -s http://localhost:8083/connectors/debezium-pg-source/status | jq .
  curl -s http://localhost:8083/connectors/opensearch-sink/status | jq .
  ```
- Check OpenSearch index exists:
  ```bash
  curl -s http://localhost:9200/products/_count
  ```
- If plugins failed to download (air-gapped), pre-build a custom Connect image with the plugins baked in.
