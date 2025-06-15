package com.example.featureflag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class RedisIntegrationTest {

    @Container
    private static final GenericContainer<?> redis =
            new GenericContainer<>("redis:7.0-alpine").withExposedPorts(6379);

    @Test
    public void testRedisSetGet() {
        String redisHost = redis.getHost();
        Integer redisPort = redis.getFirstMappedPort();

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.set("flag", "true");
            String val = jedis.get("flag");
            assertEquals("true", val);
        }
    }
}
