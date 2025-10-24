package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final Counter helloCounter;

    public HelloController(MeterRegistry registry) {
        this.helloCounter = Counter.builder("demo_hello_requests_total")
                .description("Total hello requests")
                .register(registry);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "world") String name) {
        helloCounter.increment();
        return "Hello, " + name + "!";
    }
}
