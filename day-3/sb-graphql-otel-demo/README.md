# Spring Boot GraphQL CRUD + Metrics + Traces + Postgres + OTel → Prometheus & Jaeger (Fixed)

Run everything with Docker Compose; includes all fixes (explicit @Argument names, top-level DTOs, compiler parameters, fat jar).

## Run
```bash
docker compose up --build
```

Open:
- GraphiQL: http://localhost:8080/graphiql
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000  (admin/admin)
- Jaeger: http://localhost:16686  (service: `graphql-demo`)
- Collector metrics: http://localhost:9464/metrics

## Example GraphQL
```graphql
mutation {
  createBook(input:{title:"OTel in Action", author:"Jiten", pages:320}) {
    id title author pages createdAt
  }
}
```
```graphql
query { books { id title author pages createdAt } }
```
```graphql
mutation($id:ID!) { updateBook(input:{id:$id, pages:350}) { id title pages } }
```
```graphql
mutation($id:ID!) { deleteBook(id:$id) }
```

curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { createBook(input:{title:\"GraphQL Rocks\", author:\"Jiten\", pages:280}) { id title } }"}'


📦 3. (Optional) From your frontend / Postman
	•	Endpoint: POST http://localhost:8080/graphql
	•	Header: Content-Type: application/json
	•	Body:

```
  {
  "query": "query { books { id title author pages createdAt } }"
  }
```

curl -sS -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ books { id } }"}'
