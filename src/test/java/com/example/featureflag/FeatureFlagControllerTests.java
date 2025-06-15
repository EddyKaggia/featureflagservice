package com.example.featureflag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FeatureFlagControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, FeatureFlag> redisTemplate;

    private final String API_KEY = "my-secret-key";

    @BeforeEach
    public void setup() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }

    @Test
    public void createFlag_Unauthorized_Fails() throws Exception {
        mockMvc.perform(post("/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test-flag\", \"enabled\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createFlag_AndGetFlag_Success() throws Exception {
        mockMvc.perform(post("/flags")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test-flag\", \"enabled\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-flag"))
                .andExpect(jsonPath("$.enabled").value(true));

        mockMvc.perform(get("/flags/test-flag")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    public void updateFlag_Success() throws Exception {
        // First create the flag
        mockMvc.perform(post("/flags")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"update-flag\", \"enabled\":false}"))
                .andExpect(status().isCreated());

        // Update the flag
        mockMvc.perform(put("/flags/update-flag")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    public void deleteFlag_Success() throws Exception {
        // Create flag first
        mockMvc.perform(post("/flags")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"delete-flag\", \"enabled\":true}"))
                .andExpect(status().isCreated());

        // Delete flag
        mockMvc.perform(delete("/flags/delete-flag")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk());

        // Verify flag is deleted
        mockMvc.perform(get("/flags/delete-flag")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isNotFound());
    }
}
