package demo.inventory.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {

    @Value("${app.message:Default message}")
    private String message;

    @Value("${inventory.threshold:10}")
    private int threshold;

    @GetMapping("/api/info")
    public Map<String, Object> info() {
        return Map.of(
                "service", "inventory-service",
                "message", message,
                "threshold", threshold
        );
    }
}
