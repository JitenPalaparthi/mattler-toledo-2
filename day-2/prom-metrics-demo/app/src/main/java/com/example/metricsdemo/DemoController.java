package com.example.metricsdemo;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api")
@Validated
public class DemoController {

    private final Counter ordersCreated;
    private final Timer businessTimer;
    private final DistributionSummary payloadBytes;
    private final LongTaskTimer longTaskTimer;
    private final AtomicInteger queueDepth;
    private final Random random = new Random();

    // For tracking long-running tasks
    private final AtomicLong taskSeq = new AtomicLong(0);
    private final ConcurrentMap<Long, LongTaskTimer.Sample> runningTasks = new ConcurrentHashMap<>();

    public DemoController(MeterRegistry registry) {
        // Bind basic JVM/system metrics (optional, already covered by Actuator)
        new JvmThreadMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);

        this.ordersCreated = Counter.builder("orders_created_total")
                .description("Total orders created (custom Counter)")
                .tag("region", "in")
                .register(registry);

        this.businessTimer = Timer.builder("business.timer")
                .description("Timer for simulated business operation")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.9, 0.99)
                .maximumExpectedValue(Duration.ofSeconds(5))
                .register(registry);

        this.payloadBytes = DistributionSummary.builder("payload.bytes")
                .description("DistributionSummary tracking payload sizes in bytes")
                .baseUnit("bytes")
                .publishPercentileHistogram()
                .scale(1.0)
                .register(registry);

        this.longTaskTimer = LongTaskTimer.builder("batch.longtask")
                .description("Long running task duration")
                .register(registry);

        this.queueDepth = registry.gauge("queue.depth", new AtomicInteger(0));
    }

    /**
     * Simulates order creation and records various metrics:
     * - Counter (orders_created_total)
     * - Timer (business.timer)
     * - DistributionSummary (payload.bytes)
     * - Gauge (queue.depth)
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        // track payload size
        byte[] asBytes = body.toString().getBytes();
        payloadBytes.record(asBytes.length);

        // simulate business logic and time it
        return ResponseEntity.ok(businessTimer.record(() -> {
            try {
                int work = 50 + random.nextInt(250);
                TimeUnit.MILLISECONDS.sleep(work);
                ordersCreated.increment();
                queueDepth.addAndGet(random.nextInt(3) - 1); // -1,0,+1
                if (queueDepth.get() < 0) queueDepth.set(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Map.of("status", "ok", "message", "order created");
        }));
    }

    /**
     * Starts a long-running batch task and returns a task ID.
     */
    @PostMapping("/batch/start")
    public ResponseEntity<Map<String, Object>> startBatch() {
        long id = taskSeq.incrementAndGet();
        LongTaskTimer.Sample sample = longTaskTimer.start();
        runningTasks.put(id, sample);
        return ResponseEntity.ok(Map.of(
                "status", "started",
                "taskId", id
        ));
    }

    /**
     * Stops a long-running batch task by ID.
     */
    @PostMapping("/batch/stop/{id}")
    public ResponseEntity<Map<String, Object>> stopBatch(@PathVariable long id) {
        LongTaskTimer.Sample sample = runningTasks.remove(id);
        if (sample == null) {
            return ResponseEntity.ok(Map.of("status", "invalid_id"));
        }
        sample.stop();
        return ResponseEntity.ok(Map.of("status", "stopped"));
    }

    /**
     * Simple health check endpoint.
     */
    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok("ok");
    }
}