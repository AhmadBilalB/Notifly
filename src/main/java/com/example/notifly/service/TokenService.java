package com.example.notifly.service;

import com.example.notifly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${contactly.login-url}")
    private String contactlyLoginUrl;

    /**
     * Retrieves a token from Redis or logs in to fetch a new token.
     *
     * @param username the user's username (email).
     * @param password the user's password.
     * @return the Bearer token.
     */
    public String getToken(String username, String password) {
        // Check Redis cache
        String cachedToken = redisTemplate.opsForValue().get(username);
        if (cachedToken != null) {
            log.info("Token retrieved from Redis cache for user: {}", username);
            return cachedToken;
        }

        // Fetch token from Contactly
        log.info("Token not found in cache, logging in to Contactly for user: {}", username);
        return fetchAndCacheToken(username, password);
    }

    private String fetchAndCacheToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> loginRequest = Map.of(
                "email", username,
                "password", password
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(contactlyLoginUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("token");
                log.info("Successfully fetched token: {}", token);

                // Cache token in Redis with an expiration time
                redisTemplate.opsForValue().set(username, token, Duration.ofMinutes(30));
                return token;
            }
        } catch (Exception e) {
            log.error("Failed to fetch token: {}", e.getMessage());
        }

        throw new ResourceNotFoundException("Failed to fetch token for user: " + username);
    }
}
