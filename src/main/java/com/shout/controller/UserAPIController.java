package com.shout.controller;

import com.shout.dto.UserProfileDTO;
import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User API Controller - Handles user profiles, media, and profile management
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/users/{userId}
     * Get user profile by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            UserProfileDTO userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error fetching user profile", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/users/{userId}
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDTO updateRequest,
            HttpServletRequest request) {
        try {
            // Verify user is updating their own profile
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long currentUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            // Update user
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            if (updateRequest.getName() != null) {
                user.setName(updateRequest.getName());
            }
            if (updateRequest.getProfilePicture() != null) {
                user.setProfilePicture(updateRequest.getProfilePicture());
            }
            if (updateRequest.getAccountType() != null) {
                user.setAccountType(updateRequest.getAccountType());
            }

            user = userService.saveUser(user);
            UserProfileDTO response = userService.getUserProfile(user.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating user profile", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/users/search
     * Search users with filters
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String followers) {
        try {
            List<UserProfileDTO> results = userService.searchUsers(query, genre, followers);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/users/{userId}/media
     * Get user's media items
     */
    @GetMapping("/{userId}/media")
    public ResponseEntity<?> getUserMedia(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // TODO: Return user's media items
            // This would fetch media from user.getMediaItems()
            Map<String, Object> response = new HashMap<>();
            response.put("mediaItems", new ArrayList<>());
            response.put("count", 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user media", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/users/{userId}/media
     * Upload new media
     */
    @PostMapping("/{userId}/media")
    public ResponseEntity<?> uploadMedia(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            HttpServletRequest request) {
        try {
            // Verify user is uploading their own media
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long currentUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // TODO: Implement file upload to S3 or local storage
            // Save media metadata to database
            // Check max 3 media limit
            Map<String, Object> response = new HashMap<>();
            response.put("id", System.currentTimeMillis());
            response.put("url", "uploaded_url");
            response.put("type", type);
            response.put("createdAt", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading media", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/{userId}/media/{mediaId}
     * Delete media item (cannot delete last media)
     */
    @DeleteMapping("/{userId}/media/{mediaId}")
    public ResponseEntity<?> deleteMedia(
            @PathVariable Long userId,
            @PathVariable Long mediaId,
            HttpServletRequest request) {
        try {
            // Verify user owns the media
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long currentUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            // Check if this is the last media
            // TODO: Validate media count >= 1 after deletion

            // TODO: Delete media from storage and database
            return ResponseEntity.ok(Map.of("message", "Media deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting media", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/users/{userId}/stats
     * Get user statistics (ratings, reposts, followers growth)
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", user.getId());
            stats.put("totalExchanges", 0); // TODO: Calculate from database
            stats.put("completionRate", 100); // TODO: Calculate from database
            stats.put("averageRating", user.getRating());
            stats.put("strikes", user.getStrikes());
            stats.put("followers", user.getFollowers());
            stats.put("followerGrowth", 0); // TODO: Calculate from analytics

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching user stats", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
