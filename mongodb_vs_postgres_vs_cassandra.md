# MongoDB vs PostgreSQL vs Cassandra --- Read, Write & High Availability Performance

## 📘 1. Overview

  -------------------------------------------------------------------------------
  Database         Type            Primary Use Case           Data Model
  ---------------- --------------- -------------------------- -------------------
  **MongoDB**      NoSQL           Flexible schema, fast      BSON documents
                   (Document)      writes                     

  **PostgreSQL**   Relational      Structured data, ACID      Tables & relations
                   (SQL)           compliance                 

  **Cassandra**    NoSQL           High write throughput,     Column-family model
                   (Wide-column)   distributed                
  -------------------------------------------------------------------------------

------------------------------------------------------------------------

## ⚡ 2. Write Performance

  --------------------------------------------------------------------------
  Database          Write Speed            Mechanism          Notes
  ----------------- ---------------------- ------------------ --------------
  **MongoDB**       🚀 **Excellent**       Asynchronous       Great for
                                           journaling,        insert-heavy
                                           memory-mapped      workloads like
                                           storage, MVCC      logs or IoT

  **PostgreSQL**    ⚙️ **Moderate**        WAL (Write-Ahead   Reliable but
                                           Log), synchronous  slightly
                                           commits            slower;
                                                              prioritizes
                                                              consistency

  **Cassandra**     🔥 **Excellent**       Append-only log    Extremely fast
                                           (commit log) +     for
                                           memtable +         distributed
                                           SSTables           writes
  --------------------------------------------------------------------------

------------------------------------------------------------------------

## 🔍 3. Read Performance

  --------------------------------------------------------------------------
  Database         Read Speed         Optimized When         Weakness
  ---------------- ------------------ ---------------------- ---------------
  **MongoDB**      ⚡ **Good**        Indexed queries, data  Unindexed reads
                                      fits in RAM            slow; limited
                                                             join
                                                             performance

  **PostgreSQL**   ⚡ **Excellent**   Complex queries,       Scaling reads
                                      joins, aggregates      requires
                                                             replicas

  **Cassandra**    ⚙️ **Good**        Key-based lookups      Poor for ad-hoc
                                                             queries or
                                                             secondary
                                                             indexes
  --------------------------------------------------------------------------

------------------------------------------------------------------------

## 🧩 4. High Availability (HA) & Scaling

  -----------------------------------------------------------------------------
  Database         HA Mechanism           Scaling Type         Notes
  ---------------- ---------------------- -------------------- ----------------
  **MongoDB**      Replica sets, sharding Horizontal           Supports
                                                               auto-failover;
                                                               easy read
                                                               scaling via
                                                               secondaries

  **PostgreSQL**   Streaming replication, Vertical + limited   Read replicas
                   Patroni, HAProxy       horizontal           possible; write
                                                               scaling harder

  **Cassandra**    Peer-to-peer           Horizontal           No single
                   replication                                 master; best HA
                                                               among the three
  -----------------------------------------------------------------------------

------------------------------------------------------------------------

## ⚖️ 5. Use Case Summary

  ------------------------------------------------------------------------
  Use Case                Best Choice                    Reason
  ----------------------- ------------------------------ -----------------
  Real-time analytics,    **MongoDB / Cassandra**        Fast writes,
  IoT, logs                                              distributed
                                                         architecture

  Financial systems,      **PostgreSQL**                 Strong
  strict ACID                                            consistency,
                                                         transactions

  Massive distributed     **Cassandra**                  Linear horizontal
  dataset                                                scalability

  Dynamic schema,         **MongoDB**                    No schema
  flexible JSON data                                     migration, easy
                                                         to scale
  ------------------------------------------------------------------------

------------------------------------------------------------------------

## 🏁 6. Summary Table

  --------------------------------------------------------------------------------
  Feature                  MongoDB         PostgreSQL           Cassandra
  ------------------------ --------------- -------------------- ------------------
  **Model**                Document        Relational           Column-family

  **Consistency**          Tunable         Strong               Tunable

  **Availability**         High (replica   High with HA setup   Very high
                           sets)                                (peer-to-peer)

  **Write Speed**          Excellent       Moderate             Excellent

  **Read Speed**           Good            Excellent            Good

  **Scalability**          Horizontal      Limited              Horizontal

  **Joins/Aggregations**   Limited         Excellent            Limited
  --------------------------------------------------------------------------------

------------------------------------------------------------------------

### ✅ Key Takeaways

-   MongoDB = **Best for write-heavy, semi-structured workloads**.
-   PostgreSQL = **Best for complex queries and strong ACID
    guarantees**.
-   Cassandra = **Best for distributed, massive-scale data ingestion**.

------------------------------------------------------------------------

> 🧠 **Tip:** In hybrid architectures, MongoDB is often used for fast
> ingestion and caching, while PostgreSQL is used for reporting or
> transactions.
