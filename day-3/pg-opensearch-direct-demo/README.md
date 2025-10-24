# Postgres + OpenSearch (Direct Write-Through) Demo

**No Debezium.** The Spring Boot app writes to **Postgres** and **directly indexes** each product into **OpenSearch**. On startup, it seeds **100 products** and indexes them.

## Run
```bash
docker compose up --build
```

## REST
- List from Postgres:
  `GET http://localhost:8080/api/products`
- Create → writes Postgres **and** OpenSearch:
```bash
curl -s -X POST http://localhost:8080/api/products   -H 'Content-Type: application/json'   -d '{"name":"New Book","category":"Books","price":12.5,"description":"created via API"}'
```
- Update → updates Postgres **and** OpenSearch:
```bash
curl -s -X PUT http://localhost:8080/api/products/1   -H 'Content-Type: application/json'   -d '{"price":99.0}'
```
- Search (OpenSearch):
```bash
curl "http://localhost:8080/api/search?q=category:Books%20AND%20Product&size=5"
```
