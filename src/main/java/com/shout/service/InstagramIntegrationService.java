package com.shout.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstagramIntegrationService {
    private final RestTemplate restTemplate;
    
    @Value("${instagram.api.base-url:https://graph.instagram.com}")
    private String instagramApiBaseUrl;
    
    @Value("${instagram.api.version:v18.0}")
    private String apiVersion;

    @CircuitBreaker(name = "instagram", fallbackMethod = "getInstagramProfileFallback")
    @Retry(name = "instagram")
    @Cacheable(value = "instagram_profiles", key = "#username")
    public InstagramProfile getUserProfile(String username, String accessToken) {
        try {
            String url = String.format("%s/%s/me?fields=id,username,name,profile_picture_url,biography,website,followers_count,ig_id&access_token=%s",
                instagramApiBaseUrl, apiVersion, accessToken);
            
            InstagramApiResponse response = restTemplate.getForObject(url, InstagramApiResponse.class);
            
            if (response != null && response.getId() != null) {
                return InstagramProfile.builder()
                    .id(response.getId())
                    .username(username)
                    .fullName(response.getName() != null ? response.getName() : username)
                    .profilePicUrl(response.getProfilePictureUrl())
                    .followerCount(response.getFollowersCount() != null ? response.getFollowersCount() : 0)
                    .biography(response.getBiography())
                    .websiteUrl(response.getWebsite())
                    .accountType(response.getIgId() != null ? "Business" : "Personal")
                    .build();
            }
            
            throw new RuntimeException("Invalid Instagram profile response");
        } catch (Exception e) {
            log.error("Error fetching Instagram profile for {}", username, e);
            throw new RuntimeException("Failed to fetch Instagram profile: " + e.getMessage());
        }
    }

    public InstagramProfile getInstagramProfileFallback(String username, String accessToken, Exception e) {
        log.warn("Circuit breaker fallback for user {}", username);
        return InstagramProfile.builder()
            .username(username)
            .fullName(username)
            .followerCount(0)
            .accountType("Personal")
            .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstagramApiResponse {
        private String id;
        private String username;
        private String name;
        private String profile_picture_url;
        private String biography;
        private String website;
        private Integer followers_count;
        private String ig_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class InstagramProfile {
        private String id;
        private String username;
        private String fullName;
        private String profilePicUrl;
        private Integer followerCount;
        private String biography;
        private String websiteUrl;
        private String accountType;

        public static class InstagramProfileBuilder {
            public InstagramProfile build() {
                return new InstagramProfile(id, username, fullName, profilePicUrl, followerCount, biography, websiteUrl, accountType);
            }
        }

        public static InstagramProfileBuilder builder() {
            return new InstagramProfileBuilder();
        }

        public static class InstagramProfileBuilder {
            private String id;
            private String username;
            private String fullName;
            private String profilePicUrl;
            private Integer followerCount;
            private String biography;
            private String websiteUrl;
            private String accountType;

            public InstagramProfileBuilder id(String id) { this.id = id; return this; }
            public InstagramProfileBuilder username(String username) { this.username = username; return this; }
            public InstagramProfileBuilder fullName(String fullName) { this.fullName = fullName; return this; }
            public InstagramProfileBuilder profilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; return this; }
            public InstagramProfileBuilder followerCount(Integer followerCount) { this.followerCount = followerCount; return this; }
            public InstagramProfileBuilder biography(String biography) { this.biography = biography; return this; }
            public InstagramProfileBuilder websiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; return this; }
            public InstagramProfileBuilder accountType(String accountType) { this.accountType = accountType; return this; }
            
            public InstagramProfile build() {
                return new InstagramProfile(id, username, fullName, profilePicUrl, followerCount, biography, websiteUrl, accountType);
            }
        }
    }
}
