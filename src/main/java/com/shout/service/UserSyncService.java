package com.shout.service;

import com.shout.model.User;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService {

    private final UserRepository userRepository;
    private final InstagramIntegrationService instagramService;

    @Transactional
    public User syncOrCreateUser(OAuth2User oAuth2User) {
        String username = (String) oAuth2User.getAttributes().get("login");
        if (username == null) {
            username = (String) oAuth2User.getAttributes().get("username");
        }
        String accessToken = oAuth2User.getName();

        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setAccessToken(accessToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
            user.setLastUpdatedAt(LocalDateTime.now());
            user.setIsActive(true);
            return userRepository.save(user);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setInstagramId((String) oAuth2User.getAttributes().get("id"));
        newUser.setFullName((String) oAuth2User.getAttributes().getOrDefault("name", username));
        newUser.setProfilePicUrl((String) oAuth2User.getAttributes().get("picture"));
        newUser.setCategory("General");
        newUser.setFollowerCount(((Number) oAuth2User.getAttributes().getOrDefault("followers_count", 0)).intValue());
        newUser.setBiography((String) oAuth2User.getAttributes().getOrDefault("biography", ""));
        newUser.setWebsiteUrl((String) oAuth2User.getAttributes().getOrDefault("website", ""));
        newUser.setAccountType((String) oAuth2User.getAttributes().getOrDefault("account_type", "PERSONAL"));
        newUser.setAccessToken(accessToken);
        newUser.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
        newUser.setIsActive(true);
        newUser.setAverageRating(5.0);
        newUser.setTotalRatings(0);

        User saved = userRepository.save(newUser);
        log.info("New user synced from Instagram: {}", username);
        return saved;
    }
}