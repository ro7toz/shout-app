package com.shout.service;

import com.shout.model.User;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserSyncService {
    private final UserRepository userRepository;
    private final InstagramIntegrationService instagramService;

    @Async
    public void syncOrCreateUser(String username, String accessToken) {
        try {
            // Fetch Instagram profile data
            var instagramProfile = instagramService.getUserProfile(username, accessToken);
            
            User user = userRepository.findById(username)
                .orElse(new User());
            
            user.setUsername(username);
            user.setInstagramId(instagramProfile.getId());
            user.setFullName(instagramProfile.getFullName());
            user.setProfilePicUrl(instagramProfile.getProfilePicUrl());
            user.setFollowerCount(instagramProfile.getFollowerCount());
            user.setBiography(instagramProfile.getBiography());
            user.setWebsiteUrl(instagramProfile.getWebsiteUrl());
            user.setAccountType(instagramProfile.getAccountType());
            user.setAccessToken(accessToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusMonths(2));
            user.setIsActive(true);
            
            // Infer category from bio or use default
            user.setCategory(inferCategory(instagramProfile.getBiography()));
            
            userRepository.save(user);
            log.info("User {} synced successfully", username);
        } catch (Exception e) {
            log.error("Failed to sync user {}", username, e);
            throw new RuntimeException("Failed to sync Instagram profile: " + e.getMessage());
        }
    }

    private String inferCategory(String biography) {
        if (biography == null) return "General";
        
        String bio = biography.toLowerCase();
        if (bio.contains("music") || bio.contains("singer") || bio.contains("dj")) return "Music";
        if (bio.contains("fitness") || bio.contains("gym") || bio.contains("trainer")) return "Fitness";
        if (bio.contains("beauty") || bio.contains("makeup") || bio.contains("cosmetics")) return "Beauty";
        if (bio.contains("fashion") || bio.contains("style") || bio.contains("clothes")) return "Fashion";
        if (bio.contains("food") || bio.contains("chef") || bio.contains("recipe")) return "Food";
        if (bio.contains("travel") || bio.contains("adventure") || bio.contains("explore")) return "Travel";
        if (bio.contains("tech") || bio.contains("coding") || bio.contains("developer")) return "Technology";
        if (bio.contains("business") || bio.contains("entrepreneur") || bio.contains("startup")) return "Business";
        
        return "General";
    }
}
