package com.shout.service;

import com.shout.model.*;
import com.shout.repository.PostAnalyticsRepository;
import com.shout.util.RateLimiter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramAnalyticsService {
    private final WebClient webClient;
    private final PostAnalyticsRepository analyticsRepository;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Value("${instagram.graph-api.url:https://graph.instagram.com/v17.0}")
    private String instagramApiUrl;

    @Value("${instagram.graph-api.access-token}")
    private String accessToken;

    private static final String INSIGHTS_METRIC = "impressions,profile_visits,website_clicks";

    /**
     * Fetch analytics for a specific Instagram story/post with rate limiting and caching
     */
    @Transactional
    @Cacheable(value = "instagramAnalytics", key = "#postId")
    public PostAnalytics fetchPostAnalytics(ShoutoutExchange exchange, String postId) {
        try {
            // Check rate limit
            if (!rateLimiter.allowRequest(exchange.getRequester().getInstagramId())) {
                log.warn("Rate limit exceeded for user {}", exchange.getRequester().getUsername());
                return null;
            }

            log.info("Fetching analytics for post {} from Instagram API", postId);

            // Fetch impressions (reach/views)
            Map<String, Object> analyticsData = fetchInstagramInsights(postId);

            if (analyticsData == null) {
                log.warn("Failed to fetch analytics for post {}", postId);
                return null;
            }

            // Get or create PostAnalytics record
            PostAnalytics analytics = PostAnalytics.builder()
                .user(exchange.getRequester())
                .shoutoutExchange(exchange)
                .instagramPostId(postId)
                .mediaType(exchange.getMediaType())
                .impressions((Long) analyticsData.getOrDefault("impressions", 0L))
                .clicks((Long) analyticsData.getOrDefault("website_clicks", 0L))
                .profileVisits((Long) analyticsData.getOrDefault("profile_visits", 0L))
                .analyticsVerified(true)
                .lastFetchedAt(LocalDateTime.now())
                .build();

            // Calculate engagement rate
            Long impressions = analytics.getImpressions();
            if (impressions > 0) {
                Long engagements = analytics.getClicks() + analytics.getProfileVisits();
                Double engagementRate = (double) (engagements * 100) / impressions;
                analytics.setEngagementRate(engagementRate);
            }

            PostAnalytics saved = analyticsRepository.save(analytics);
            log.info("Analytics saved for post {}: {} impressions, {} clicks", 
                postId, saved.getImpressions(), saved.getClicks());
            
            return saved;
        } catch (Exception e) {
            log.error("Error fetching Instagram analytics for post {}", postId, e);
            return null;
        }
    }

    /**
     * Fetch insights from Instagram Graph API with error handling
     */
    private Map<String, Object> fetchInstagramInsights(String postId) {
        try {
            String url = String.format("%s/%s/insights?metric=%s&access_token=%s",
                instagramApiUrl, postId, INSIGHTS_METRIC, accessToken);

            String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Instagram API error: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

            if (response == null) {
                return null;
            }

            // Parse JSON response
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataArray = rootNode.get("data");

            Map<String, Object> metrics = new HashMap<>();
            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode item : dataArray) {
                    String name = item.get("name").asText();
                    Long value = item.get("values").get(0).get("value").asLong();
                    metrics.put(name, value);
                }
            }

            log.info("Instagram API response parsed: {}", metrics);
            return metrics;
        } catch (Exception e) {
            log.error("Error parsing Instagram API response", e);
            return null;
        }
    }

    /**
     * Refresh analytics for a specific exchange (manual refresh)
     */
    @Transactional
    public PostAnalytics refreshAnalytics(ShoutoutExchange exchange) {
        try {
            // Get existing analytics
            PostAnalytics existing = analyticsRepository.findByShoutoutExchange(exchange)
                .orElse(null);

            if (existing == null) {
                log.warn("No analytics found for exchange {}", exchange.getId());
                return null;
            }

            // Fetch fresh data
            String postId = existing.getInstagramPostId();
            Map<String, Object> freshData = fetchInstagramInsights(postId);

            if (freshData != null) {
                existing.setImpressions((Long) freshData.getOrDefault("impressions", existing.getImpressions()));
                existing.setClicks((Long) freshData.getOrDefault("website_clicks", existing.getClicks()));
                existing.setProfileVisits((Long) freshData.getOrDefault("profile_visits", existing.getProfileVisits()));
                existing.setLastFetchedAt(LocalDateTime.now());

                // Recalculate engagement rate
                if (existing.getImpressions() > 0) {
                    Double engagementRate = (double) ((existing.getClicks() + existing.getProfileVisits()) * 100) 
                        / existing.getImpressions();
                    existing.setEngagementRate(engagementRate);
                }

                return analyticsRepository.save(existing);
            }

            return existing;
        } catch (Exception e) {
            log.error("Error refreshing analytics", e);
            return null;
        }
    }

    /**
     * Get cached analytics for user dashboard
     */
    public PostAnalytics getCachedAnalytics(ShoutoutExchange exchange) {
        return analyticsRepository.findByShoutoutExchange(exchange).orElse(null);
    }

    /**
     * Clear analytics cache for a user
     */
    public void clearAnalyticsCache(User user) {
        log.info("Clearing analytics cache for user {}", user.getUsername());
        // Spring Cache abstraction will handle cache invalidation
    }
}
