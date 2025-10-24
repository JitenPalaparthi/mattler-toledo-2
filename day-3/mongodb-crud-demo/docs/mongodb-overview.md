# MongoDB: Why it’s Good, and How It Differs from Traditional (Relational) Databases

## Why MongoDB is great
1. **Schema flexibility**  
   - Store documents with evolving fields without costly ALTER TABLE migrations.
   - Great for agile teams and fast-changing product schemas.

2. **Developer productivity**  
   - JSON-like (BSON) documents map naturally to objects.  
   - Powerful query language with aggregation pipeline, indexes, and text search.

3. **Horizontal scalability**  
   - Sharding and replica sets are built-in.  
   - Easy high availability via replica sets (automatic failover).

4. **Rich features**  
   - Secondary indexes (compound, TTL, text, geospatial).  
   - Change streams for event-driven apps.  
   - Transactions (multi-document) when needed.  
   - Time-series collections, schema validation, and more.

5. **Operational simplicity**  
   - Single binary for most features; great managed options (Atlas).  
   - Good tooling (Mongo Shell, Compass, Mongo-Express, drivers).

## When MongoDB shines
- Product catalogs, user profiles, IoT/event ingestion, content management, feature flags, rapid prototyping, flexible/heterogeneous records, hierarchical data.

## When a relational DB may be better
- Strongly normalized data with complex multi-table joins.  
- Heavy cross-entity ACID transactions and strict referential integrity.  
- Mature ecosystems around SQL analytics/warehousing patterns.

---

## MongoDB vs. Relational Databases (RDBMS)

| Aspect | MongoDB (Document) | Relational (SQL) |
|-------|---------------------|------------------|
| Data model | Documents (BSON), nested structures | Tables, rows, columns |
| Schema | Flexible (optional validation) | Rigid (DDL migrations) |
| Relationships | Embedded docs or manual refs; no joins by default (lookup in aggregation) | Foreign keys, joins built-in |
| Transactions | Single-doc atomic by default; multi-doc transactions supported since 4.0 | ACID transactions across tables |
| Scaling | Horizontal (sharding) first-class; replica sets | Traditionally vertical; clustering/partitioning varies |
| Indexes | Single, compound, sparse, TTL, text, geo | B-tree, hash, full-text (vendor-dependent), etc. |
| Query | Rich doc queries + Aggregation Pipeline | SQL (standardized) |
| Best for | Evolving schemas, hierarchical/JSON data, microservices, content/event data | Strong integrity, complex joins, OLTP with stable schemas |
| Tooling | Compass, Mongo Shell, Mongo-Express, Atlas | psql/MySQL CLI, pgAdmin, SSMS; many mature tools |
| Data shape | Denormalized, embed where it makes sense | Normalized with foreign keys |

### Performance notes
- Reads/writes on a single document are atomic and fast.  
- Embedding related data eliminates joins → fewer round trips.  
- For high-cardinality filters and analytics, design indexes carefully and consider the Aggregation Pipeline.

### Design tips
- **Model around application queries**, not around 3NF.  
- Embed when child data is typically read with the parent and doesn’t grow unbounded.  
- Reference when many-to-many or unbounded growth exists.  
- Use compound indexes that match your common query patterns.  
- Add TTL indexes for ephemeral data (sessions, events).

---

## Running locally via Docker Compose

- **MongoDB** exposed on `27017` with root user `root/rootpass` (for admin).  
- **Mongo-Express** UI on `http://localhost:8081`.  
- App connects with `mongodb://mongo:27017/demo` by default (no auth needed for the demo DB).  
- Change `MONGODB_URI` in compose or `application.yml` to point elsewhere.

---

## API Summary (from this demo)
- Create: `POST /api/products`
- Read (by id): `GET /api/products/{id}`
- Read (by SKU): `GET /api/products/sku/{sku}`
- Search + paging: `GET /api/products?q=term&min=0&max=999&page=0&size=10`
- Replace: `PUT /api/products/{id}`
- Patch: `PATCH /api/products/{id}` (partial update)
- Delete: `DELETE /api/products/{id}`
- Bulk create: `POST /api/products/bulk`

This setup is a solid baseline for production patterns (validation, paging, partial updates, unique indexes). Add authentication, observability, and schema validation rules as you evolve.
