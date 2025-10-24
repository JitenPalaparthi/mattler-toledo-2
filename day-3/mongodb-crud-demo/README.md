# MongoDB CRUD Demo (Spring Boot 3.3.x)

A full-fledged CRUD API using **Spring Boot + Spring Data MongoDB**, packaged with **Docker Compose** that runs **MongoDB** and **Mongo-Express** UI.

## Run
```bash
docker compose up -d --build
```

Open:
- API: http://localhost:8080/api/products
- Mongo Express: http://localhost:8081

## API Endpoints
- `POST /api/products` → create a product
- `POST /api/products/bulk` → bulk create
- `GET /api/products/{id}` → get by id
- `GET /api/products/sku/{sku}` → get by SKU
- `GET /api/products?q=phone&min=100&max=2000&page=0&size=10` → search + paging
- `PUT /api/products/{id}` → full replace
- `PATCH /api/products/{id}` → partial update (send only fields to change)
- `DELETE /api/products/{id}` → delete

### Example JSON
```json
{
  "name": "iPhone 15",
  "sku": "IP15-128-BLK",
  "price": 799.00,
  "tags": ["phone", "apple"]
}
```

### Quick demo
```bash
curl -X POST localhost:8080/api/products   -H 'Content-Type: application/json'   -d '{"name":"iPhone 15","sku":"IP15-128-BLK","price":799,"tags":["phone","apple"]}'

curl "localhost:8080/api/products?q=phone&min=100&max=2000&page=0&size=5"
```
