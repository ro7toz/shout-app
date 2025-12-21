package com.shout.controller;

import com.shout.dto.UserProfileDTO;
import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User API Controller - User search, discovery, and filtering
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserAPIController {
   
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
   
    /**
     * GET /api/users/search
     * Search users with advanced filtering
     * Supports: query, genre, followers, repostType
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String followers,
            @RequestParam(required = false) String repostType) {
        try {
            // Get base search results
            List<UserProfileDTO> results = userService.searchUsers(query, genre, followers);
           
            // Filter by repostType if provided
            if (repostType != null && !repostType.isEmpty()) {
                results = filterByRepostType(results, repostType);
            }
           
            log.info("Search completed: query={}, genre={}, repostType={}, results={}",
                query, genre, repostType, results.size());
           
            return ResponseEntity.ok(Map.of(
                "count", results.size(),
                "users", results
            ));
           
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * GET /api/users/{userId}
     * Get user profile with all details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
           
            UserProfileDTO profile = convertToProfileDTO(user);
           
            return ResponseEntity.ok(profile);
           
        } catch (Exception e) {
            log.error("Error fetching user profile", e);
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
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
           
            // TODO: Implement media retrieval logic
            // Return user's media items (posts, stories, reels)
           
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "media", user.getMediaItems()
            ));
           
        } catch (Exception e) {
            log.error("Error fetching user media", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * Helper: Filter users by repost type
     * story: All plans
     * post/reel: PRO only
     */
    private List<UserProfileDTO> filterByRepostType(List<UserProfileDTO> users, String repostType) {
        return users.stream()
            .filter(user -> {
                if ("story".equalsIgnoreCase(repostType)) {
                    return true; // All plans support stories
                } else if ("post".equalsIgnoreCase(repostType) || "reel".equalsIgnoreCase(repostType)) {
                    return "PRO".equals(user.getPlanType()); // Only PRO for posts/reels
                }
                return true;
            })
            .collect(Collectors.toList());
    }
   
    /**
     * Helper: Convert User to UserProfileDTO
     */
    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setPlanType(user.getPlanType());
        dto.setFollowers(user.getFollowers());
        dto.setAccountType(user.getAccountType());
        dto.setIsVerified(user.getIsVerified());
        dto.setRating(user.getRating());
        dto.setStrikes(user.getStrikeCount());
        return dto;
    }
}
