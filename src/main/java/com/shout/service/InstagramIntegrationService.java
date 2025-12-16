package com.shout.service;

import com.shout.model.User;
import com.shout.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramIntegrationService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private static final String INSTAGRAM_GRAPH_API = "https://graph.instagram.com/me";

    @Cacheable(value = "instagram_profiles", key = "#username")
    @CircuitBreaker(name = "instagram", fallbackMethod = "getFallbackInstagramData")
    @Retry(name = "instagram")
    public User fetchInstagramData(String username, String accessToken) {
        log.info("Fetching Instagram data for user: {}", username);

        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                return existingUser.get();
            }

            User user = new User();
            user.setUsername(username);
            user.setFullName(username);
            user.setCategory("General");
            user.setFollowerCount(0);
            user.setAverageRating(5.0);
            user.setIsActive(true);
            return user;

        } catch (Exception e) {
            log.error("Error fetching Instagram data for {}: {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch Instagram data", e);
        }
    }

    public User getFallbackInstagramData(String username, String accessToken, Throwable ex) {
        log.warn("Circuit breaker activated or Instagram API failed for {}, using fallback", username);

        Optional<User> cachedUser = userRepository.findByUsername(username);
        if (cachedUser.isPresent()) {
            return cachedUser.get();
        }

        User fallbackUser = new User();
        fallbackUser.setUsername(username);
        fallbackUser.setCategory("Offline");
        fallbackUser.setIsActive(false);
        return fallbackUser;
    }
}