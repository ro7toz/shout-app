package com.shout.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shout.dto.AuthResponse;
import com.shout.model.User;
import com.shout.repository.UserRepository;
import com.shout.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

/**
 * Instagram OAuth Authentication Service (REAL)
 * Handles Instagram OAuth flow using actual Instagram API credentials
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramAuthService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final InstagramGraphService instagramGraphService;

    @Value("${instagram.client-id}")
    private String clientId;

    @Value("${instagram.client-secret}")
    private String clientSecret;

    @Value("${instagram.redirect-uri}")
    private String redirectUri;

    /**
     * Handle Instagram OAuth callback
     * Exchange authorization code for access token and create/update user
     */
    @Transactional
    public AuthResponse handleCallback(String code, String state) {
        try {
            log.info("🔐 Processing Instagram OAuth callback...");

            // Step 1: Exchange code for access token
            String shortLivedToken = exchangeCodeForToken(code);
            log.info("✅ Obtained short-lived access token");

            // Step 2: Exchange for long-lived token (60 days)
            String longLivedToken = instagramGraphService.exchangeForLongLivedToken(shortLivedToken, clientSecret);
            log.info("✅ Exchanged for long-lived token");

            // Step 3: Fetch user profile from Instagram
            InstagramGraphService.InstagramProfile igProfile = instagramGraphService.fetchUserProfile(longLivedToken);
            log.info("✅ Fetched Instagram profile: {}", igProfile.getUsername());

            // Step 4: Try to fetch business profile (may fail if not business account)
            try {
                InstagramGraphService.InstagramProfile businessProfile = instagramGraphService.fetchBusinessProfile(longLivedToken);
                igProfile = businessProfile; // Use business profile if available
                log.info("✅ Fetched business profile with {} followers", igProfile.getFollowerCount());
            } catch (Exception e) {
                log.info("ℹ️ Not a business account, using basic profile");
            }

            // Step 5: Create or update user in database
            User user = createOrUpdateUser(igProfile, longLivedToken);
            log.info("✅ User created/updated: {}", user.getUsername());

            // Step 6: Generate JWT token
            String jwtToken = jwtTokenProvider.generateToken(user.getId());

            // Step 7: Build response
            AuthResponse response = new AuthResponse();
            response.setSuccess(true);
            response.setMessage("Authentication successful");
            response.setToken(jwtToken);

            AuthResponse.UserInfoResponse userInfo = new AuthResponse.UserInfoResponse();
            userInfo.setUserId(user.getId().toString());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setProfilePicture(user.getProfilePicture());
            userInfo.setFollowerCount(Long.valueOf(user.getFollowerCount()));
            userInfo.setBio(user.getBio());
            userInfo.setInstagramId(user.getInstagramId());

            response.setUser(userInfo);

            log.info("🎉 Authentication complete for user: {}", user.getUsername());
            return response;

        } catch (Exception e) {
            log.error("❌ Instagram authentication failed", e);
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Authentication failed: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Exchange authorization code for short-lived access token
     */
    private String exchangeCodeForToken(String code) {
        try {
            String url = "https://api.instagram.com/oauth/access_token";

            String formData = String.format(
                "client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s",
                clientId, clientSecret, redirectUri, code
            );

            String response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("No response from Instagram token exchange");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            
            if (rootNode.has("error_message")) {
                throw new RuntimeException("Instagram API error: " + rootNode.get("error_message").asText());
            }

            return rootNode.get("access_token").asText();

        } catch (Exception e) {
            log.error("Failed to exchange code for token", e);
            throw new RuntimeException("Token exchange failed: " + e.getMessage());
        }
    }

    /**
     * Create or update user in database with Instagram data
     */
    @Transactional
    private User createOrUpdateUser(InstagramGraphService.InstagramProfile igProfile, String accessToken) {
        // Check if user exists by Instagram ID
        User user = userRepository.findByInstagramId(igProfile.getInstagramId())
                .orElse(null);

        boolean isNewUser = (user == null);

        if (isNewUser) {
            user = new User();
            user.setCreatedAt(LocalDateTime.now());
            log.info("📝 Creating new user for Instagram: {}", igProfile.getUsername());
        } else {
            log.info("🔄 Updating existing user: {}", user.getUsername());
        }

        // Update user data
        user.setInstagramId(igProfile.getInstagramId());
        user.setInstagramUsername(igProfile.getUsername());
        user.setUsername(igProfile.getUsername());
        user.setName(igProfile.getName() != null ? igProfile.getName() : igProfile.getUsername());
        user.setBio(igProfile.getBio());
        user.setProfilePicture(igProfile.getProfilePictureUrl());
        user.setFollowerCount(igProfile.getFollowerCount() != null ? igProfile.getFollowerCount() : 0);
        user.setAccountType(igProfile.getAccountType());
        user.setInstagramAccessToken(accessToken);
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        // Set defaults for new users
        if (isNewUser) {
            user.setEmail(igProfile.getUsername() + "@instagram.user"); // Placeholder email
            user.setPlanType("BASIC");
            user.setStrikeCount(0);
            user.setAccountBanned(false);
            user.setSocialLoginBanned(false);
            user.setDailyRequestsSent(0);
            user.setDailyRequestsAccepted(0);
            user.setRating(0.0);
            user.setTotalRatings(0);
            user.setIsVerified(false);
        }

        user = userRepository.save(user);
        log.info("💾 User saved: {} (ID: {})", user.getUsername(), user.getId());

        return user;
    }

    /**
     * Exchange short-lived token for long-lived token
     * Public method that can be called from controller
     */
    public AuthResponse exchangeForLongLivedToken(String shortLivedToken) {
        try {
            String longLivedToken = instagramGraphService.exchangeForLongLivedToken(shortLivedToken, clientSecret);
            
            AuthResponse response = new AuthResponse();
            response.setSuccess(true);
            response.setMessage("Token exchange successful");
            response.setToken(longLivedToken);
            
            return response;
        } catch (Exception e) {
            log.error("Failed to exchange token", e);
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Token exchange failed: " + e.getMessage());
            return errorResponse;
        }
    }
}
