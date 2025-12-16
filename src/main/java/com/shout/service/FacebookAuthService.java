package com.shout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.shout.dto.FacebookAuthRequest;
import com.shout.dto.AuthResponse;
import com.shout.dto.AuthResponse.UserInfoResponse;
import com.shout.model.User;
import com.shout.repository.UserRepository;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling Facebook authentication
 * Validates tokens, syncs users, and integrates with Instagram data
 */
@Service
public class FacebookAuthService {

    @Autowired
    private UserRepository userRepository;

    @Value("${facebook.app-id}")
    private String facebookAppId;

    @Value("${facebook.app-secret}")
    private String facebookAppSecret;

    @Value("${facebook.api-version}")
    private String apiVersion;

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Validate Facebook access token by calling Facebook Graph API
     * This ensures the token is valid and hasn't been revoked
     * 
     * @param accessToken Facebook access token
     * @return true if token is valid, false otherwise
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            // Call Facebook Graph API to validate token
            String url = String.format(
                "https://graph.facebook.com/%s/debug_token?input_token=%s&access_token=%s|%s",
                apiVersion, accessToken, facebookAppId, facebookAppSecret
            );

            Map response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("data")) {
                Map data = (Map) response.get("data");
                Boolean isValid = (Boolean) data.get("is_valid");
                return isValid != null && isValid;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Error validating Facebook token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sync or create user from Facebook authentication request
     * Creates new user if doesn't exist, updates if exists
     * 
     * @param request Facebook auth request with user details
     * @return AuthResponse with user info
     */
    public AuthResponse syncOrCreateUser(FacebookAuthRequest request) {
        try {
            // Check if user already exists by Facebook ID
            Optional<User> existingUser = userRepository.findByFacebookId(request.getUserId());
            
            User user;
            if (existingUser.isPresent()) {
                // Update existing user
                user = existingUser.get();
                user.setFacebookAccessToken(request.getAccessToken());
                user.setFacebookTokenExpiresAt(System.currentTimeMillis() + (request.getExpiresIn() * 1000));
                user.setName(request.getFbName());
                user.setEmail(request.getFbEmail());
                user.setProfilePicture(request.getFbProfilePicture());
            } else {
                // Create new user
                user = new User();
                user.setUsername(generateUsernameFromEmail(request.getFbEmail()));
                user.setFacebookId(request.getUserId());
                user.setFacebookAccessToken(request.getAccessToken());
                user.setFacebookTokenExpiresAt(System.currentTimeMillis() + (request.getExpiresIn() * 1000));
                user.setName(request.getFbName());
                user.setEmail(request.getFbEmail());
                user.setProfilePicture(request.getFbProfilePicture());
                user.setFullName(request.getFbName());
                user.setIsActive(true);
            }

            // Save user to database
            user = userRepository.save(user);

            // Build user info response
            UserInfoResponse userInfo = new UserInfoResponse(
                user.getUsername(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getFollowerCount() != null ? user.getFollowerCount().longValue() : 0L
            );
            userInfo.setInstagramId(user.getFacebookId());
            userInfo.setBio(user.getBio());

            return new AuthResponse(true, "Authentication successful", user.getUsername(), userInfo);

        } catch (Exception e) {
            return new AuthResponse(
                false,
                "Failed to sync user: " + e.getMessage(),
                null,
                null
            );
        }
    }

    /**
     * Refresh user session by validating existing token
     * 
     * @param accessToken Current Facebook access token
     * @return AuthResponse with success status
     */
    public AuthResponse refreshSession(String accessToken) {
        try {
            // Validate the access token
            if (!validateAccessToken(accessToken)) {
                return new AuthResponse(
                    false,
                    "Invalid or expired Facebook token",
                    null,
                    null
                );
            }

            // Find user by Facebook access token
            Optional<User> userOpt = userRepository.findByFacebookAccessToken(accessToken);
            
            if (!userOpt.isPresent()) {
                return new AuthResponse(
                    false,
                    "User not found",
                    null,
                    null
                );
            }

            User user = userOpt.get();

            // Build user info response
            UserInfoResponse userInfo = new UserInfoResponse(
                user.getUsername(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getFollowerCount() != null ? user.getFollowerCount().longValue() : 0L
            );

            return new AuthResponse(true, "Session refreshed", user.getUsername(), userInfo);

        } catch (Exception e) {
            return new AuthResponse(
                false,
                "Session refresh failed: " + e.getMessage(),
                null,
                null
            );
        }
    }

    /**
     * Generate username from email
     * Used when creating new user from Facebook login
     * Falls back to UUID if email not available
     * 
     * @param email User's email
     * @return Generated username
     */
    private String generateUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            String baseUsername = email.substring(0, email.indexOf("@")).toLowerCase();
            // Check if username exists, if so add random suffix
            if (userRepository.existsById(baseUsername)) {
                return baseUsername + "_" + UUID.randomUUID().toString().substring(0, 8);
            }
            return baseUsername;
        }
        // Fallback: generate from UUID
        return "user_" + UUID.randomUUID().toString().substring(0, 12);
    }
}
