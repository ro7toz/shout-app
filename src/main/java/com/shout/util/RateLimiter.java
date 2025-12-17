package com.shout.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentMap<String, Bucket> cache = new ConcurrentHashMap<>();

    private static final int REQUESTS_PER_HOUR = 100; // Instagram API limit

    /**
     * Check if request is allowed for user (rate limiting)
     * Instagram Graph API: 100 requests per hour per user
     */
    public boolean allowRequest(String userId) {
        try {
            String key = "rate_limit:" + userId;
            Bucket bucket = cache.get(key);

            if (bucket == null) {
                // Create new bucket with refill every hour
                Bandwidth limit = Bandwidth.classic(REQUESTS_PER_HOUR, Refill.intervally(REQUESTS_PER_HOUR, Duration.ofHours(1)));
                bucket = Bucket4j.builder()
                    .addLimit(limit)
                    .build();
                
                cache.putIfAbsent(key, bucket);
                bucket = cache.get(key);
            }

            boolean allowed = bucket.tryConsume(1);
            
            if (!allowed) {
                log.warn("Rate limit exceeded for user {}", userId);
            } else {
                log.debug("Request allowed for user {}. Tokens remaining: {}", userId, bucket.getAvailableTokens());
            }

            return allowed;
        } catch (Exception e) {
            log.error("Error checking rate limit", e);
            // Allow request on error (fail open)
            return true;
        }
    }

    /**
     * Get remaining requests for user
     */
    public Long getRemainingRequests(String userId) {
        String key = "rate_limit:" + userId;
        Bucket bucket = cache.get(key);
        return bucket != null ? bucket.getAvailableTokens() : REQUESTS_PER_HOUR;
    }

    /**
     * Reset rate limit for user (admin action)
     */
    public void resetRateLimit(String userId) {
        String key = "rate_limit:" + userId;
        cache.remove(key);
        log.info("Rate limit reset for user {}", userId);
    }
}
