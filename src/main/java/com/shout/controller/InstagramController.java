package com.shout.controller;

import com.shout.model.*;
import com.shout.repository.ShoutoutExchangeRepository;
import com.shout.service.InstagramAnalyticsService;
import com.shout.util.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/instagram")
@RequiredArgsConstructor
@Slf4j
public class InstagramController {
    private final InstagramAnalyticsService analyticsService;
    private final ShoutoutExchangeRepository exchangeRepository;
    private final RateLimiter rateLimiter;

    /**
     * Fetch analytics for a specific exchange
     */
    @PostMapping("/analytics/{exchangeId}")
    public ResponseEntity<?> fetchAnalytics(@RequestAttribute("user") User user,
                                           @PathVariable Long exchangeId,
                                           @RequestParam String instagramPostId) {
        try {
            ShoutoutExchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

            // Authorization check
            if (!exchange.getRequester().getUsername().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
            }

            // Check rate limit
            Long remaining = rateLimiter.getRemainingRequests(user.getInstagramId());
            if (remaining <= 0) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse("Rate limit exceeded. Try again in 1 hour", remaining));
            }

            // Fetch analytics from Instagram API
            PostAnalytics analytics = analyticsService.fetchPostAnalytics(exchange, instagramPostId);

            if (analytics == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to fetch Instagram analytics", -1L));
            }

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error fetching Instagram analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), -1L));
        }
    }

    /**
     * Refresh analytics for exchange
     */
    @PostMapping("/analytics/{exchangeId}/refresh")
    public ResponseEntity<?> refreshAnalytics(@RequestAttribute("user") User user,
                                             @PathVariable Long exchangeId) {
        try {
            ShoutoutExchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

            // Authorization
            if (!exchange.getRequester().getUsername().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
            }

            PostAnalytics analytics = analyticsService.refreshAnalytics(exchange);

            if (analytics == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to refresh");
            }

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error refreshing analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get rate limit status
     */
    @GetMapping("/rate-limit/status")
    public ResponseEntity<?> getRateLimitStatus(@RequestAttribute("user") User user) {
        try {
            Long remaining = rateLimiter.getRemainingRequests(user.getInstagramId());
            Long limit = 100L;
            Long used = limit - remaining;

            Map<String, Object> status = new HashMap<>();
            status.put("remaining_requests", remaining);
            status.put("total_limit", limit);
            status.put("used_requests", used);
            status.put("reset_in_hours", 1);

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting rate limit status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get cached analytics
     */
    @GetMapping("/analytics/{exchangeId}/cached")
    public ResponseEntity<?> getCachedAnalytics(@RequestAttribute("user") User user,
                                               @PathVariable Long exchangeId) {
        try {
            ShoutoutExchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

            PostAnalytics analytics = analyticsService.getCachedAnalytics(exchange);

            if (analytics == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Analytics not cached");
            }

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting cached analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private static class ErrorResponse {
        public String error;
        public Long remaining;

        public ErrorResponse(String error, Long remaining) {
            this.error = error;
            this.remaining = remaining;
        }
    }
}
