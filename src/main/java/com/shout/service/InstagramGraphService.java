package com.shout.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Instagram Graph API Service
 * Handles all Instagram API interactions - profile fetch, media retrieval, post verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramGraphService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${instagram.graph-api-url:https://graph.instagram.com}")
    private String graphApiUrl;

    @Value("${instagram.api-version:v21.0}")
    private String apiVersion;

    /**
     * Fetch Instagram user profile
     * @param accessToken Instagram access token
     * @return InstagramProfile with user details
     */
    public InstagramProfile fetchUserProfile(String accessToken) {
        try {
            String url = String.format("%s/%s/me?fields=id,username,account_type,media_count&access_token=%s",
                    graphApiUrl, apiVersion, accessToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Failed to fetch Instagram profile - null response");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            
            InstagramProfile profile = new InstagramProfile();
            profile.setInstagramId(rootNode.get("id").asText());
            profile.setUsername(rootNode.get("username").asText());
            profile.setAccountType(rootNode.has("account_type") ? rootNode.get("account_type").asText() : "PERSONAL");
            profile.setMediaCount(rootNode.has("media_count") ? rootNode.get("media_count").asInt() : 0);

            log.info("✅ Fetched Instagram profile: {}", profile.getUsername());
            return profile;

        } catch (Exception e) {
            log.error("❌ Error fetching Instagram profile", e);
            throw new RuntimeException("Failed to fetch Instagram profile: " + e.getMessage());
        }
    }

    /**
     * Fetch Instagram Business Account profile (with follower count)
     * Requires Instagram Business account linked to Facebook Page
     */
    public InstagramProfile fetchBusinessProfile(String accessToken) {
        try {
            String url = String.format("%s/%s/me?fields=id,username,name,biography,followers_count,follows_count,media_count,profile_picture_url&access_token=%s",
                    graphApiUrl, apiVersion, accessToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Failed to fetch business profile");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            
            InstagramProfile profile = new InstagramProfile();
            profile.setInstagramId(rootNode.get("id").asText());
            profile.setUsername(rootNode.get("username").asText());
            profile.setName(rootNode.has("name") ? rootNode.get("name").asText() : rootNode.get("username").asText());
            profile.setBio(rootNode.has("biography") ? rootNode.get("biography").asText() : "");
            profile.setFollowerCount(rootNode.has("followers_count") ? rootNode.get("followers_count").asInt() : 0);
            profile.setFollowingCount(rootNode.has("follows_count") ? rootNode.get("follows_count").asInt() : 0);
            profile.setMediaCount(rootNode.has("media_count") ? rootNode.get("media_count").asInt() : 0);
            profile.setProfilePictureUrl(rootNode.has("profile_picture_url") ? rootNode.get("profile_picture_url").asText() : "");
            profile.setAccountType("BUSINESS");

            log.info("✅ Fetched business profile: {} ({} followers)", profile.getUsername(), profile.getFollowerCount());
            return profile;

        } catch (Exception e) {
            log.error("❌ Error fetching business profile", e);
            throw new RuntimeException("Failed to fetch business profile: " + e.getMessage());
        }
    }

    /**
     * Fetch user's recent media items
     * @param accessToken Instagram access token
     * @param limit Number of media items to fetch (default 25)
     * @return List of InstagramMedia
     */
    public List<InstagramMedia> fetchUserMedia(String accessToken, int limit) {
        try {
            String url = String.format("%s/%s/me/media?fields=id,caption,media_type,media_url,thumbnail_url,permalink,timestamp&limit=%d&access_token=%s",
                    graphApiUrl, apiVersion, limit, accessToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                log.warn("No media response from Instagram API");
                return new ArrayList<>();
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataArray = rootNode.get("data");

            List<InstagramMedia> mediaList = new ArrayList<>();
            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode item : dataArray) {
                    InstagramMedia media = new InstagramMedia();
                    media.setMediaId(item.get("id").asText());
                    media.setMediaType(item.has("media_type") ? item.get("media_type").asText() : "IMAGE");
                    media.setMediaUrl(item.has("media_url") ? item.get("media_url").asText() : "");
                    media.setThumbnailUrl(item.has("thumbnail_url") ? item.get("thumbnail_url").asText() : "");
                    media.setCaption(item.has("caption") ? item.get("caption").asText() : "");
                    media.setPermalink(item.has("permalink") ? item.get("permalink").asText() : "");
                    media.setTimestamp(item.has("timestamp") ? item.get("timestamp").asText() : "");
                    
                    mediaList.add(media);
                }
            }

            log.info("✅ Fetched {} media items from Instagram", mediaList.size());
            return mediaList;

        } catch (Exception e) {
            log.error("❌ Error fetching Instagram media", e);
            return new ArrayList<>();
        }
    }

    /**
     * Verify if a post exists and is still live
     * @param mediaId Instagram media ID
     * @param accessToken User's access token
     * @return true if post exists and is accessible
     */
    public boolean verifyPostExists(String mediaId, String accessToken) {
        try {
            String url = String.format("%s/%s/%s?fields=id&access_token=%s",
                    graphApiUrl, apiVersion, mediaId, accessToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            boolean exists = response != null && response.contains("\"id\"");
            log.debug("Post verification for {}: {}", mediaId, exists);
            return exists;

        } catch (Exception e) {
            log.warn("Post verification failed for {}: {}", mediaId, e.getMessage());
            return false;
        }
    }

    /**
     * Exchange short-lived token for long-lived token
     * Long-lived tokens last 60 days
     */
    public String exchangeForLongLivedToken(String shortLivedToken, String clientSecret) {
        try {
            String url = String.format("%s/access_token?grant_type=ig_exchange_token&client_secret=%s&access_token=%s",
                    graphApiUrl, clientSecret, shortLivedToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Failed to exchange token");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            String longLivedToken = rootNode.get("access_token").asText();

            log.info("✅ Exchanged for long-lived token");
            return longLivedToken;

        } catch (Exception e) {
            log.error("❌ Error exchanging token", e);
            throw new RuntimeException("Failed to exchange token: " + e.getMessage());
        }
    }

    /**
     * Refresh long-lived token (extends for another 60 days)
     */
    public String refreshLongLivedToken(String longLivedToken) {
        try {
            String url = String.format("%s/refresh_access_token?grant_type=ig_refresh_token&access_token=%s",
                    graphApiUrl, longLivedToken);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Failed to refresh token");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            String newToken = rootNode.get("access_token").asText();

            log.info("✅ Refreshed long-lived token");
            return newToken;

        } catch (Exception e) {
            log.error("❌ Error refreshing token", e);
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }
    }

    // DTO Classes
    public static class InstagramProfile {
        private String instagramId;
        private String username;
        private String name;
        private String bio;
        private String profilePictureUrl;
        private Integer followerCount;
        private Integer followingCount;
        private Integer mediaCount;
        private String accountType;

        // Getters and Setters
        public String getInstagramId() { return instagramId; }
        public void setInstagramId(String instagramId) { this.instagramId = instagramId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        
        public String getProfilePictureUrl() { return profilePictureUrl; }
        public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
        
        public Integer getFollowerCount() { return followerCount; }
        public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }
        
        public Integer getFollowingCount() { return followingCount; }
        public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }
        
        public Integer getMediaCount() { return mediaCount; }
        public void setMediaCount(Integer mediaCount) { this.mediaCount = mediaCount; }
        
        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
    }

    public static class InstagramMedia {
        private String mediaId;
        private String mediaType; // IMAGE, VIDEO, CAROUSEL_ALBUM
        private String mediaUrl;
        private String thumbnailUrl;
        private String caption;
        private String permalink;
        private String timestamp;

        // Getters and Setters
        public String getMediaId() { return mediaId; }
        public void setMediaId(String mediaId) { this.mediaId = mediaId; }
        
        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }
        
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
        
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        
        public String getPermalink() { return permalink; }
        public void setPermalink(String permalink) { this.permalink = permalink; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
