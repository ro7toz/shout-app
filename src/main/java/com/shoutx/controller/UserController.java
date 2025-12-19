package com.shoutx.controller;

import com.shoutx.dto.UserProfileDTO;
import com.shoutx.dto.UpdateProfileRequest;
import com.shoutx.entity.User;
import com.shoutx.entity.UserPhoto;
import com.shoutx.service.UserService;
import com.shoutx.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<String> photoUrls = user.getPhotos().stream()
                .map(UserPhoto::getPhotoUrl)
                .collect(Collectors.toList());
        
        UserProfileDTO profile = UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .accountType(user.getAccountType())
                .isVerified(user.getIsVerified())
                .rating(user.getRating().doubleValue())
                .photos(photoUrls)
                .build()
                .build();
        
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable Long userId, 
                                                          @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(userId, request.getName(), request.getBio(), 
                request.getAccountType());
        
        List<String> photoUrls = user.getPhotos().stream()
                .map(UserPhoto::getPhotoUrl)
                .collect(Collectors.toList());
        
        UserProfileDTO profile = UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .accountType(user.getAccountType())
                .photos(photoUrls)
                .build()
                .build();
        
        return ResponseEntity.ok(profile);
    }
    
    @PostMapping("/{userId}/photos")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long userId, @RequestParam MultipartFile file) throws IOException {
        UserPhoto photo = userPhotoService.uploadPhoto(userId, file);
        return ResponseEntity.ok(new Object() {
            public final Long id = photo.getId();
            public final String url = photo.getPhotoUrl();
        });
    }
    
    @DeleteMapping("/{userId}/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long userId, @PathVariable Long photoId) {
        userPhotoService.deletePhoto(userId, photoId);
        return ResponseEntity.ok(new Object() {
            public final boolean success = true;
            public final String message = "Photo deleted successfully";
        });
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDTO>> searchUsers(@RequestParam(required = false) String genre) {
        User.AccountType accountType = genre != null ? User.AccountType.valueOf(genre) : null;
        List<User> users = userService.searchUsers(accountType);
        
        List<UserProfileDTO> profiles = users.stream()
                .map(u -> UserProfileDTO.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .username(u.getUsername())
                        .profilePictureUrl(u.getProfilePictureUrl())
                        .accountType(u.getAccountType())
                        .isVerified(u.getIsVerified())
                        .rating(u.getRating().doubleValue())
                        .photos(u.getPhotos().stream().map(UserPhoto::getPhotoUrl).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profiles);
    }
}
