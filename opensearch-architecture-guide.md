# OpenSearch Architecture — Deep Dive

This guide explains OpenSearch’s **components**, **data flow**, and **why searches are fast**. It’s written to be practical and precise, with mental models you can use when sizing, debugging, or tuning.

---

## TL;DR

- OpenSearch is a **distributed search & analytics engine** built on **Apache Lucene**.
- Data is stored in **indices**, split into **shards** (Lucene indexes) with **replicas** for HA and parallel reads.
- **Coordinating nodes** fan out queries to shards in parallel; results are reduced and returned.
- **Inverted indexes** + **segment immutability** + **OS page cache + mmap** + **columnar doc values** make reads very fast.

---

## Big Picture Components

### Cluster
A logical group of nodes that share a **cluster state** and a unique `cluster.name`. The cluster is responsible for **routing**, **allocation**, **balancing**, and **metadata** coherence.

### Node (process)
A JVM process that joins a cluster. A node may play one or more **roles**:

- **Cluster Manager role** (formerly “master”): Maintains global cluster state (nodes, indices, mappings, shard assignments). Only a small subset of nodes need this role. It **does not** handle data queries itself.
- **Data role**: Stores shards and serves index/search operations. Sub-flavors (by deployment policy) often include:
  - **Hot** (frequent writes/queries, high-IO SSD, more CPU/RAM)
  - **Warm** (less frequent, bigger/cheaper disks)
  - **Cold** (rarely accessed, cheapest storage; search latency tolerates higher I/O)
- **Ingest role**: Runs **ingest pipelines** (enrich, grok, geoip, etc.) before indexing docs.
- **ML role** (optional): Executes machine-learning workloads/plugins.
- **Remote-eligible role** (cross-cluster search/replication scenarios).
- **Coordinating-only**: A node with all data/manager roles disabled; used purely to **fan out/collect** requests (handy for API gateways).

> In small clusters, a single node can hold multiple roles. In production, separate **cluster-manager** and **data** nodes for stability.

### Index
A named logical collection of documents (e.g., `logs-2025.10.24`). An index is partitioned into **primary shards**; each primary may have zero or more **replica shards**. Replicas give **HA** and **parallel read** throughput.

### Shard
A shard is a **Lucene index** — the real on-disk structure. Inside a shard, data lives in **segments**. Queries and aggregations execute at shard level and are merged at the coordinator.

### Segment (Lucene)
Immutable files written during indexing. New docs go to **in-memory buffers**, then are **refreshed** (≈ 1s by default) to new segments. Periodic **merges** compact small segments into fewer large segments to improve search performance.

### OpenSearch Dashboards
Web UI for visualization, Dev Tools (Console), and management. It talks to the cluster via the same REST API.

### Plugins
Security, ingestion, ML (e.g., k-NN), alerting, index management, performance analyzer, etc. Many production distributions enable the **Security** plugin (roles/users/TLS) by default.

---

## Data Flow: Write Path

1. **Client request** (`POST index/_doc` or bulk API) hits a **coordinating node** (could be any node).
2. The coordinator uses **routing** (hash of `_id` unless a custom routing key is provided) to determine the **target primary shard**.
3. The request is forwarded to the **primary shard** on a data node.
4. The primary:
   - Adds the operation to the **translog** (write-ahead log for durability).
   - Indexes the doc into the in-memory index buffer (Lucene RAM buffer).
   - Acks when the translog is fsynced (depending on `index.translog.durability`).
5. The primary **replicates** the operation to its **replica shards** (synchronous by default), then returns success to the coordinator.
6. **Refresh** (default ~1s, controlled by `index.refresh_interval`) makes the new doc searchable by writing a new **segment** and publishing a new **searcher**. Until refresh, the doc is **durable** but not yet visible in search.

**Why write performance is good:** writes are **append-only** to segments + translog; replicas handle the same operation in parallel. Merge operates in the background.

---

## Data Flow: Read Path (Query/Aggregation)

1. A **search** request hits a coordinating node.
2. **Fan-out:** the coordinator determines which shards contain the index/indices and sends the query to **one active copy** of each shard (primary or any replica).
3. Each shard executes the query using Lucene, producing **top-N hits** and **partial aggs**.
4. The coordinator **reduces** (merges) the partial results (e.g., merges sorted hit lists, combines aggregations).
5. Final response is returned to the client.

**Parallelism:** Every shard processes independently; replicas enable **increased parallel read capacity**. The coordinator reduces results in-memory using streaming merges.

---

## Inside a Shard: Why Reads Are Fast

OpenSearch’s read performance inherits Lucene’s **search-optimized structures**:

- **Inverted Index**: For each **term**, store a **posting list** of documents containing it. Queries like `category:Books AND name:Product` are intersections of posting lists.
- **Term Dictionary (FST)**: Efficient term lookup with a finite-state transducer (compact + prefix compression).
- **Skip Lists & Blocked Postings**: Skip data to jump over sections quickly during intersections.
- **Doc Values (columnar storage)**: On-disk columnar representation per field for **aggregations**, sorting, and scripting — avoids loading full _source.
- **Segment Immutability**: Segments don’t change after refresh; allows **lock-free** searchers, heavy OS page-cache usage, and predictable memory-mapping.
- **Memory-mapped (mmap) I/O**: Lucene files are mmap’d; the **OS page cache** serves hot data without JVM heap overhead.
- **Query Cache & Request Cache**:
  - **Shard request cache** caches full query+agg results when eligible (e.g., `size:0` aggs).
  - **Query cache** stores frequently used filter bitsets for fast boolean composition.
- **BM25** (default) scoring: Efficient relevance model for free-text fields.

Together, these reduce disk seeks and CPU per query, yielding **very low-latency reads** under concurrency.

---

## Refresh, Flush, and Merge (Indexing Mechanics)

- **Refresh**: Publishes new segments to make recent writes searchable (default ≈ 1s). Lower it for lower-latency visibility; increase it for higher indexing throughput.
- **Flush**: Forces translog to roll + fsync commit points; good for durability and recovery time.
- **Merge**: Background process consolidating small segments into larger ones (fewer file handles, faster searches). Controlled by merge policy and throttles.

**Tuning tips:**
- For heavy bulk indexing, set `index.refresh_interval=-1` (disable refresh), index in bulk, then set back (and force a manual refresh) to avoid many tiny segments.
- Use **Bulk API** and appropriate `number_of_replicas` during initial load to maximize throughput.

---

## Routing, Sharding, and Replication

- **Shards**: Set **# of primary shards** at index creation time. **Replicas** can be changed dynamically.
- **Routing**: By default, `_id` → hash → shard. You can provide a **routing key** to co-locate related docs in the same shard (faster joins-by-application and aggregations).
- **Replicas**: Improve availability and **read throughput**; searches can hit primaries or replicas.
- **Rebalancing**: Cluster managers move shards to keep data/nodes balanced; **allocation filters** and **ILM/ISM** move indices across hot/warm/cold tiers.

---

## Caches (Where Speed Comes From)

- **OS Page Cache**: Most critical. Lucene’s mmapped files are served from the kernel cache if hot → minimal syscalls/IO.
- **Fielddata / Doc Values**: For field sorting/aggregations. Prefer **doc values**-enabled fields for analytics (the default for keyword/numeric/date).
- **Shard Request Cache**: Great for repeated dashboards with the same filters/intervals.
- **Query Cache**: Caches filter bitsets used in boolean queries; keep filters stable for better hit rate.

**Rule of thumb:** Give OS page cache **RAM**; avoid over-inflating JVM heap. Typical deployments allocate **~50% RAM to heap** and leave the rest to OS cache (varies by workload).

---

## Ingest Pipelines

- Defined per index; process docs **before indexing**.
- Common processors: `grok`, `geoip`, `set`, `remove`, `rename`, `script`, `enrich`.
- Attach via `?pipeline=name` on indexing or set a default pipeline for an index.
- Use dedicated **ingest nodes** for heavy processing so data nodes focus on search/storage.

---

## Security & Multi-Tenancy (Plugin)

- **Users/Roles** with index-level and document-level security.
- **TLS** for node-to-node and HTTP.
- **Fine-grained** controls for multi-tenant setups.
- Enabled by default in many distributions; often disabled only in local demos.

---

## Cluster Coordination

- **Cluster-manager nodes** elect a single active manager and maintain **cluster state** (index metadata, shard routing table, templates, ISM policies, etc.).
- Cluster state updates propagate to all nodes; **versioned** to prevent split-brain.
- Keep at least **3 cluster-manager-eligible** nodes for quorum in production (odd numbers).

---

## Observability & Operations

- **Node stats** (`_nodes/stats`), **cluster health** (`_cluster/health`), **cat APIs** (`/_cat/indices`, `/_cat/shards`, `/_cat/nodes`).
- **Index State Management (ISM)**: Automate rolling over, moving indices across hot→warm→cold tiers, retention policies.
- **Performance Analyzer** plugin: resource usage and bottlenecks.
- **Snapshots**: Incremental backups/restores to S3/HDFS-compatible stores.

---

## Why OpenSearch Is Fast for Reads — Recap

1. **Shard-level parallelism** across nodes/replicas.
2. **Lucene inverted index** with efficient postings & term dictionary (FST).
3. **Immutable segments** → lock-free concurrent search; background merges improve locality.
4. **Memory-mapped I/O** and **OS page cache** keep hot data in RAM **outside** JVM heap.
5. **Doc values (columnar)** accelerate aggregations & sorting on structured fields.
6. **Smart caching** (filter/query caches, request cache) for repeated dashboards/filters.
7. **Coordinating node fan-out + partial reduce** minimizes tail latency.

---

## Practical Sizing Tips

- Start with **1–5 primary shards** per index (don’t overshard). Add **replicas** for read throughput/HA.
- Keep JVM heap **modest** (e.g., 16–32 GB) and leave RAM for OS cache.
- Use **hot-warm-cold** data tiering + **rollovers** for time-based indices.
- For analytics-heavy workloads, model fields with **keyword/numeric/date + doc values**; avoid analyzing fields that you only aggregate/sort on.
- Prefer **Bulk API** for ingestion; tune `refresh_interval` during bulk loads.

---

## Glossary

- **Index**: Logical collection of docs.
- **Shard**: Lucene index (physical partition); primary or replica.
- **Segment**: Immutable files inside a shard; created at refresh; merged over time.
- **Doc values**: Columnar storage for fields, used by aggregations/sorts.
- **Translog**: Write-ahead log for durability.
- **Coordinator**: Node that receives a request, fans out to shards, merges results.
- **Cluster-manager**: Maintains cluster state; orchestrates shard allocation/moves.

---

*If you want this tailored to your deployment (node counts, heap sizes, shard counts, refresh settings), I can add a “production blueprint” section with ready-to-apply configs.*
