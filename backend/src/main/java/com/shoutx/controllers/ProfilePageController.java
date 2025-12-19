package com.shoutx.controllers;

import com.shoutx.models.User;
import com.shoutx.models.MediaItem;
import com.shoutx.services.UserService;
import com.shoutx.services.MediaService;
import com.shoutx.dtos.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pages/profile")
public class ProfilePageController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private MediaService mediaService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId, Authentication authentication) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<MediaItem> media = mediaService.getUserMedia(userId);
        ProfileDTO profileDTO = new ProfileDTO(
            user.getId(),
            user.getUsername(),
            user.getProfilePicture(),
            user.getFollowers(),
            user.getRating(),
            user.getStrikes(),
            user.getPlanType().toString(),
            media
        );

        return ResponseEntity.ok(profileDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
        @PathVariable Long userId,
        @RequestBody User updatedUser,
        Authentication authentication
    ) {
        Long currentUserId = Long.parseLong(authentication.getName());
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        User updated = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{userId}/media")
    public ResponseEntity<?> uploadMedia(
        @PathVariable Long userId,
        @RequestBody MediaItem mediaItem,
        Authentication authentication
    ) {
        Long currentUserId = Long.parseLong(authentication.getName());
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        MediaItem saved = mediaService.addMedia(userId, mediaItem);
        return ResponseEntity.ok(saved);
    }
}
