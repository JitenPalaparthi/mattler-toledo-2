# RabbitMQ vs Kafka — Practical Comparison

| Feature | RabbitMQ | Kafka |
|----------|-----------|-------|
| **Core model** | Message broker that routes messages through **exchanges → queues** | Distributed **log** storing ordered records in **partitions** |
| **Delivery mechanism** | Pushes messages to consumers (via queue) | Consumers pull messages from partitions |
| **Ordering guarantee** | Order preserved **per queue**; multiple consumers can disrupt order | Strong order guarantee **per partition** |
| **Message retention** | Removed once acknowledged (configurable TTL or queue length) | Retained for configured **time or size** even after consumption |
| **Replayability** | Not built-in; must requeue or republish | Native — just reset consumer offset and reprocess |
| **Throughput** | Optimized for transactional workloads & low-latency tasks | Extremely high throughput, designed for continuous data streams |
| **Routing capabilities** | Flexible — supports **direct**, **topic**, **fanout**, **headers** exchanges | Simple topic-based pub/sub model (partition key only) |
| **Scaling model** | Scale consumers horizontally per queue (compete for messages) | Scale consumers per partition (1 consumer per partition in a group) |
| **Persistence model** | Messages persisted in queues; once consumed, usually deleted | Immutable log — consumers maintain offsets independently |
| **DLQ (Dead Letter Queue)** | Built-in via Dead Letter Exchanges (DLX) | Requires manual pattern or stream processor |
| **Backpressure handling** | Built-in via consumer acks and prefetch count | Managed by consumer lag and offsets |
| **Use cases** | Command, Task queue, Event notifications, Request/Response | Event sourcing, Analytics, Stream processing, CDC pipelines |
| **Best for** | **Real-time, short-lived tasks** and routing | **High-volume, durable event streams** and replay |
| **Protocol** | AMQP 0-9-1, supports multiple languages | Proprietary TCP binary protocol (clients via Kafka libraries) |
| **Admin UI** | RabbitMQ Management (http://localhost:15672) | CLI tools, Confluent Control Center, or third-party UIs |
| **Conceptual summary** | “Send this message to whoever is listening.” | “Record this event for anyone who wants to read it — now or later.” |

---

## 🧭 Key Takeaways

- **RabbitMQ**: Great for microservices messaging, routing, RPC, task queues, and transient messages.  
  👉 Focused on **delivery guarantees**, **routing flexibility**, and **work distribution**.

- **Kafka**: Ideal for event sourcing, audit logs, stream analytics, and high-throughput ingestion.  
  👉 Focused on **durability**, **scalability**, and **event replay**.

---

## 🔄 When to choose which

| Goal | Recommended System |
|------|--------------------|
| Process tasks in real time, one-time processing | 🐇 RabbitMQ |
| Keep a durable event history for replay | 🧱 Kafka |
| Need flexible routing (topic/fanout) | 🐇 RabbitMQ |
| Need massive throughput (GB/s) | 🧱 Kafka |
| Complex event pipelines / analytics | 🧱 Kafka |
| Work queues, command patterns, DLQs | 🐇 RabbitMQ |

---

### 🧩 Quick visual mental model

```
RabbitMQ:   Producer → Exchange → Queue → Consumer (ack, delete)

Kafka:      Producer → Topic(partition) → Consumer Group (offset, commit)
```
