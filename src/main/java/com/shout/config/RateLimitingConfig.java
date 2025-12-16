package com.shout.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Configuration using Bucket4j
 * Protects API from DDoS attacks and abuse
 */
@Configuration
public class RateLimitingConfig {

    /**
     * API Rate Limiter: 100 requests per minute per IP
     */
    @Bean
    public Bandwidth apiLimit() {
        return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    }

    /**
     * Auth Endpoint Rate Limiter: 10 requests per minute per IP
     * (to prevent brute force attacks)
     */
    @Bean
    public Bandwidth authLimit() {
        return Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
    }

    /**
     * Instagram Integration Rate Limiter: 1000 requests per hour
     * (respects Instagram API limits)
     */
    @Bean
    public Bandwidth instagramLimit() {
        return Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofHours(1)));
    }

    /**
     * Bucket cache for tracking rate limits per IP/user
     */
    @Bean
    public ConcurrentHashMap<String, Bucket> bucketCache() {
        return new ConcurrentHashMap<>();
    }
}
