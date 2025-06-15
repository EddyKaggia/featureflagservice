// Feature Flag Service in Java (Spring Boot)

package com.example.featureflag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FeatureFlagApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagApplication.class, args);
    }
}

@RestController
@RequestMapping("/flags")
class FeatureFlagController {

    @Autowired
    private RedisTemplate<String, FeatureFlag> redisTemplate;

    private static final String API_KEY = System.getenv("API_KEY");

    private boolean isAuthorized(String apiKey) {
        return API_KEY.equals(apiKey);
    }

    @PostMapping
    public ResponseEntity<?> createFlag(@RequestHeader(value = "x-api-key", required = false) String apiKey, @RequestBody FeatureFlagRequest request) {
        if (apiKey == null || !isAuthorized(apiKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        if (redisTemplate.hasKey(request.getName())) return ResponseEntity.status(HttpStatus.CONFLICT).body("Flag already exists");
        FeatureFlag flag = new FeatureFlag(UUID.randomUUID().toString(), request.getName(), request.isEnabled(), new Date(), null);
        redisTemplate.opsForValue().set(request.getName(), flag);
        return ResponseEntity.status(HttpStatus.CREATED).body(flag);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getFlag(@RequestHeader("x-api-key") String apiKey, @PathVariable String name) {
        if (!isAuthorized(apiKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        FeatureFlag flag = redisTemplate.opsForValue().get(name);
        if (flag == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flag not found");
        return ResponseEntity.ok(Collections.singletonMap("enabled", flag.isEnabled()));
    }

    @PutMapping("/{name}")
    public ResponseEntity<?> updateFlag(@RequestHeader("x-api-key") String apiKey, @PathVariable String name, @RequestBody FeatureFlagRequest request) {
        if (!isAuthorized(apiKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        FeatureFlag flag = redisTemplate.opsForValue().get(name);
        if (flag == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flag not found");
        flag.setEnabled(request.isEnabled());
        flag.setUpdatedAt(new Date());
        redisTemplate.opsForValue().set(name, flag);
        return ResponseEntity.ok(flag);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteFlag(@RequestHeader("x-api-key") String apiKey, @PathVariable String name) {
        if (!isAuthorized(apiKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(name))) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flag not found");
        redisTemplate.delete(name);
        return ResponseEntity.ok("Flag deleted");
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics(@RequestHeader("x-api-key") String apiKey) {
        if (!isAuthorized(apiKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        Long size = redisTemplate.keys("*").stream().count();
        return ResponseEntity.ok(Collections.singletonMap("flagCount", size));
    }
}

class FeatureFlagRequest {
    private String name;
    private boolean enabled;

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void setName(String name) { this.name = name; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
