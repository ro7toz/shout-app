package com.shout.controller;

import com.shout.dto.UserProfileDTO;
import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserAPIController {
   
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
   
    @GetMapping("/search")
    public ResponseEntity searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String followers,
            @RequestParam(required = false) String repostType) {
        try {
            List results = userService.searchUsers(query, genre, followers);
           
            if (repostType != null && !repostType.isEmpty()) {
                results = filterByRepostType(results, repostType);
            }
           
            return ResponseEntity.ok(Map.of(
                "count", results.size(),
                "users", results
            ));
           
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    @GetMapping("/{userId}")
    public ResponseEntity getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserProfileDTO profile = convertToProfileDTO(user);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Error fetching user profile", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    private List filterByRepostType(List users, String repostType) {
        return users.stream()
            .filter(user -> {
                if ("story".equalsIgnoreCase(repostType)) {
                    return true;
                } else if ("post".equalsIgnoreCase(repostType) || "reel".equalsIgnoreCase(repostType)) {
                    return "PRO".equals(user.getPlanType());
                }
                return true;
            })
            .collect(Collectors.toList());
    }
   
    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setPlanType(user.getPlanType());
        dto.setFollowers(user.getFollowerCount());
        dto.setAccountType(user.getAccountType());
        dto.setIsVerified(user.getIsVerified());
        dto.setRating(user.getRating());
        dto.setStrikes(user.getStrikeCount());
        return dto;
    }
}
