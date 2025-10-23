
# 📊 Spring Boot + Prometheus Metrics Overview

This document explains all the **metrics** exposed by the `prom-metrics-demo` application using **Spring Boot Actuator**, **Micrometer**, and **Prometheus**.

---

## 🧭 1. Types of Metrics

| Category | Description |
|-----------|--------------|
| **Default metrics** | Automatically provided by Spring Boot Actuator + Micrometer (JVM, system, HTTP server, etc.) |
| **Custom metrics** | The ones explicitly defined in `DemoController.java` |

---

## 🧩 2. Default Metrics (Spring Boot + Micrometer)

| Metric Name (prefix) | Description | Examples |
|----------------------|-------------|-----------|
| **`jvm_memory_*`** | Heap/non-heap memory usage, buffer pools, metaspace | `jvm_memory_used_bytes`, `jvm_memory_max_bytes` |
| **`jvm_threads_*`** | Live, daemon, and peak thread counts | `jvm_threads_live_threads`, `jvm_threads_daemon_threads` |
| **`system_cpu_*`** | CPU usage and available cores | `system_cpu_count`, `system_cpu_usage` |
| **`process_cpu_*`** | CPU time and usage for this JVM process | `process_cpu_usage`, `process_cpu_seconds_total` |
| **`http_server_requests_*`** | Metrics for each HTTP endpoint (method, status, URI, etc.) | `http_server_requests_seconds_count`, `http_server_requests_seconds_sum`, `http_server_requests_seconds_bucket` |
| **`logback_events_total`** | Log events by level | `logback_events_total{level="INFO"}` |
| **`application_ready_time_seconds`** | App startup times | — |

---

## ⚙️ 3. Custom Metrics (from DemoController)

The demo defines five key custom metrics.

### 1️⃣ `orders_created_total` (Counter)

**Type:** Counter  
**Description:** Total number of orders created (`/api/order`).

**Example Output:**
```
# HELP orders_created_total Total orders created (custom Counter)
# TYPE orders_created_total counter
orders_created_total{region="in",application="metrics-demo"} 45.0
```

**Use Cases:**
- Count processed orders/messages/events.
- Compute order rate per second using Prometheus `rate()`.

---

### 2️⃣ `business.timer` (Timer)

**Type:** Timer (Histogram internally)  
**Description:** Time taken to process each order (latency).

**Example Output:**
```
# HELP business_timer_seconds Timer for simulated business operation
# TYPE business_timer_seconds histogram
business_timer_seconds_bucket{le="0.1"} 5
business_timer_seconds_bucket{le="0.5"} 40
business_timer_seconds_count 45
business_timer_seconds_sum 6.73
```

**Use Cases:**
- Measure latency percentiles (p50, p90, p99).
- Create Prometheus queries using `histogram_quantile()`.

---

### 3️⃣ `payload.bytes` (DistributionSummary)

**Type:** DistributionSummary  
**Description:** Tracks payload sizes (bytes) of incoming requests.

**Example Output:**
```
# HELP payload_bytes DistributionSummary tracking payload sizes in bytes
# TYPE payload_bytes summary
payload_bytes_count 45
payload_bytes_sum 6750
payload_bytes_max 320
```

**Use Cases:**
- Monitor average request size.
- Detect unusually large payloads.

---

### 4️⃣ `queue.depth` (Gauge)

**Type:** Gauge  
**Description:** Current simulated queue length.

**Example Output:**
```
# HELP queue_depth Current simulated queue size
# TYPE queue_depth gauge
queue_depth 2.0
```

**Use Cases:**
- Track backlog size.
- Alert if `queue.depth` exceeds threshold.

---

### 5️⃣ `batch.longtask` (LongTaskTimer)

**Type:** LongTaskTimer  
**Description:** Duration of long-running batch operations.

**Example Output:**
```
# HELP batch_longtask_seconds Long running task duration
# TYPE batch_longtask_seconds gauge
batch_longtask_seconds_active_count 1
batch_longtask_seconds_duration_sum 8.123
```

**Use Cases:**
- Measure how long background tasks run.
- Monitor number of active long-running jobs.

---

## 🔍 4. Histogram Details

Because histograms are enabled (`publishPercentileHistogram()`), each timer or summary metric produces:
- `_bucket` → counts per duration/size bucket.
- `_count` → total samples.
- `_sum` → total observed value sum.

Example for `business.timer`:
```
business_timer_seconds_bucket{le="0.1"} 5
business_timer_seconds_bucket{le="0.25"} 15
business_timer_seconds_bucket{le="0.5"} 40
business_timer_seconds_count 45
business_timer_seconds_sum 6.73
```

---

## 📈 5. Example Prometheus / Grafana Queries

| Metric | Query Example | Purpose |
|---------|----------------|----------|
| `orders_created_total` | `rate(orders_created_total[1m])` | Orders per second |
| `business_timer_seconds_bucket` | `histogram_quantile(0.99, sum(rate(business_timer_seconds_bucket[5m])) by (le))` | p99 latency |
| `payload_bytes_sum / payload_bytes_count` | `rate(payload_bytes_sum[1m]) / rate(payload_bytes_count[1m])` | Avg payload size |
| `queue_depth` | `queue_depth` | Current queue backlog |
| `batch_longtask_seconds_active_count` | `batch_longtask_seconds_active_count` | Running tasks |

---

## ⚡ Summary Table

| Metric | Type | Description |
|--------|------|-------------|
| `orders_created_total` | Counter | Number of orders created |
| `business.timer` | Timer | Duration of order creation (latency) |
| `payload.bytes` | DistributionSummary | Size of incoming payloads |
| `queue.depth` | Gauge | Simulated queue depth |
| `batch.longtask` | LongTaskTimer | Duration + count of long-running tasks |
| `http_server_requests_seconds_*` | Timer | HTTP request latency (all endpoints) |
| `jvm_*`, `system_*`, `process_*` | Gauges, Counters | JVM and system-level metrics |

---

## 📚 References

- [Spring Boot Actuator Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Docs](https://micrometer.io/docs/concepts)
- [Prometheus Query Language](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana Dashboard Examples](https://grafana.com/grafana/dashboards/)

---

✅ With these metrics, you can build a full observability dashboard for:
- **Performance** (p99 latency, throughput)
- **Health** (CPU, memory, threads)
- **Business metrics** (orders, queue depth, payload size)
- **Batch operations** (active jobs, duration)
