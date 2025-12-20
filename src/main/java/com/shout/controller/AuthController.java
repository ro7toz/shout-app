package com.shout.controller;

import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller - Handles OAuth and media selection
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
   
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
   
    /**
     * POST /api/auth/select-media
     * Store selected media (1-3 items) after OAuth
     */
    @PostMapping("/select-media")
    public ResponseEntity<?> selectMediaAfterAuth(
            @RequestBody Map<String, Object> requestBody) {
        try {
            @SuppressWarnings("unchecked")
            List<String> mediaIds = (List<String>) requestBody.get("mediaIds");
            Long userId = ((Number) requestBody.get("userId")).longValue();
           
            // Validate media count (1-3)
            if (mediaIds == null || mediaIds.isEmpty() || mediaIds.size() > 3) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Please select 1-3 media items")
                );
            }
           
            // Get user
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
           
            // Store selected media
            // TODO: Implement media storage logic
            // Associate mediaIds with user
           
            log.info("User {} selected {} media items", userId, mediaIds.size());
           
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Media selection successful");
            response.put("mediaCount", mediaIds.size());
            response.put("userId", userId);
           
            return ResponseEntity.ok(response);
           
        } catch (Exception e) {
            log.error("Error during media selection", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * GET /api/auth/callback/instagram
     * Instagram OAuth callback handler
     */
    @GetMapping("/callback/instagram")
    public ResponseEntity<?> handleInstagramCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        try {
            // Validate authorization code
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Missing authorization code")
                );
            }
           
            // TODO: Exchange code for access token
            // Call Instagram Graph API with code
            // Receive access_token
           
            // TODO: Fetch user data from Instagram
            // Call Instagram API to get user info
            // Extract: id, username, name, profile_picture_url
           
            // TODO: Create or update user in database
            // Check if user exists by Instagram ID
            // If exists: update token and data
            // If new: create user with BASIC plan
           
            log.info("Instagram OAuth callback received for code: {}", code);
           
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Instagram authentication successful");
            response.put("status", "pending_media_selection");
            // response.put("token", jwtToken);
            response.put("nextStep", "Select 1-3 media items");
           
            return ResponseEntity.ok(response);
           
        } catch (Exception e) {
            log.error("Instagram OAuth callback error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}