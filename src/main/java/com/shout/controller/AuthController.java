package com.shout.controller;

import com.shout.dto.UserProfileDTO;
import com.shout.dto.LoginRequest;
import com.shout.dto.LoginResponse;
import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller - Handles login, signup, logout, and token refresh
 * Supports Instagram OAuth 2.0 integration
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /api/auth/login
     * Login with Instagram OAuth token
     * Returns JWT token and user profile
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for Instagram user: {}", loginRequest.getInstagramUsername());

            // Validate Instagram token (in production, verify with Instagram API)
            if (loginRequest.getInstagramToken() == null || loginRequest.getInstagramToken().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Instagram token required"));
            }

            // Find or create user
            Optional<User> userOptional = userService.findByInstagramUsername(loginRequest.getInstagramUsername());
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                // Update Instagram data if available
                if (loginRequest.getFollowers() != null) {
                    user.setFollowers(loginRequest.getFollowers());
                }
                if (loginRequest.getProfilePicture() != null) {
                    user.setProfilePicture(loginRequest.getProfilePicture());
                }
                user = userService.saveUser(user);
            } else {
                // New user - create account
                user = new User();
                user.setUsername(loginRequest.getInstagramUsername());
                user.setInstagramUsername(loginRequest.getInstagramUsername());
                user.setInstagramAccessToken(loginRequest.getInstagramToken());
                user.setPlanType("BASIC");
                user.setStrikes(0);
                user.setRating(0.0);
                user.setFollowers(loginRequest.getFollowers() != null ? loginRequest.getFollowers() : 0);
                user.setProfilePicture(loginRequest.getProfilePicture());
                user.setAccountType("Creator");
                user.setIsVerified(false);
                user = userService.saveUser(user);
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getId());
            long expiresIn = jwtTokenProvider.getExpirationTime();

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setExpiresIn(expiresIn);
            response.setUser(userService.getUserProfile(user.getId()));

            log.info("User logged in successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/signup
     * Create new user account with Instagram data
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody LoginRequest signupRequest) {
        try {
            log.info("Signup attempt for Instagram user: {}", signupRequest.getInstagramUsername());

            // Check if user already exists
            Optional<User> existingUser = userService.findByInstagramUsername(signupRequest.getInstagramUsername());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User already exists"));
            }

            // Create new user
            User user = new User();
            user.setUsername(signupRequest.getInstagramUsername());
            user.setInstagramUsername(signupRequest.getInstagramUsername());
            user.setInstagramAccessToken(signupRequest.getInstagramToken());
            user.setPlanType("BASIC"); // All new users start with BASIC plan
            user.setStrikes(0);
            user.setRating(0.0);
            user.setFollowers(signupRequest.getFollowers() != null ? signupRequest.getFollowers() : 0);
            user.setProfilePicture(signupRequest.getProfilePicture());
            user.setAccountType(signupRequest.getAccountType() != null ? signupRequest.getAccountType() : "Creator");
            user.setIsVerified(false);
            user.setDailyRequestsSent(0);
            user.setDailyRequestsAccepted(0);

            user = userService.saveUser(user);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getId());
            long expiresIn = jwtTokenProvider.getExpirationTime();

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setExpiresIn(expiresIn);
            response.setUser(userService.getUserProfile(user.getId()));

            log.info("New user created: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Signup error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * Logout current user (invalidates token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token != null) {
                // TODO: Add token to blacklist for additional security
                log.info("User logged out");
            }
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/refresh-token
     * Refresh JWT token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String newToken = jwtTokenProvider.generateToken(userId);
            long expiresIn = jwtTokenProvider.getExpirationTime();

            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("expiresIn", expiresIn);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Token refresh error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/auth/me
     * Get current authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            UserProfileDTO userProfile = userService.getUserProfile(userId);

            return ResponseEntity.ok(userProfile);

        } catch (Exception e) {
            log.error("Error fetching current user", e);
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
    }

    /**
     * POST /api/auth/verify-instagram-token
     * Verify Instagram token with Instagram API
     */
    @PostMapping("/verify-instagram-token")
    public ResponseEntity<?> verifyInstagramToken(@RequestBody Map<String, String> request) {
        try {
            String instagramToken = request.get("token");
            // TODO: Implement actual Instagram API verification
            // This would call Instagram Graph API to verify token validity
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (Exception e) {
            log.error("Instagram token verification error", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
