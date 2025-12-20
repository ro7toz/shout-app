package com.shout.service;

import com.shout.dto.UserMediaDTO;
import com.shout.dto.UserProfileDTO;
import com.shout.model.User;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service - Complete implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
   
    private final UserRepository userRepository;
   
    /**
     * Find user by ID
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
   
    /**
     * Find user by Instagram username
     */
    public Optional<User> findByInstagramUsername(String instagramUsername) {
        return userRepository.findByInstagramUsername(instagramUsername);
    }
   
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
   
    /**
     * Save user
     */
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }
   
    /**
     * Get user profile DTO
     */
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
       
        return convertToProfileDTO(user);
    }
   
    /**
     * Search users with filters
     */
    public List<UserProfileDTO> searchUsers(String query, String genre, String followers) {
        List<User> users = new ArrayList<>();
       
        // Parse follower range
        Integer minFollowers = 0;
        Integer maxFollowers = Integer.MAX_VALUE;
       
        if (followers != null && !followers.isEmpty()) {
            String[] range = followers.split("-");
            if (range.length == 2) {
                try {
                    minFollowers = Integer.parseInt(range[0]);
                    maxFollowers = Integer.parseInt(range[1]);
                } catch (NumberFormatException e) {
                    log.warn("Invalid follower range: {}", followers);
                }
            }
        }
       
        // Apply filters
        if (genre != null && !genre.isEmpty()) {
            users = userRepository.findByCategoryAndFollowerCountRange(genre, minFollowers, maxFollowers);
        } else {
            users = userRepository.findByFollowerCountRange(minFollowers, maxFollowers);
        }
       
        // Filter by query if provided
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            users = users.stream()
                .filter(u -> u.getName().toLowerCase().contains(lowerQuery) ||
                            u.getUsername().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        }
       
        return users.stream()
            .map(this::convertToProfileDTO)
            .collect(Collectors.toList());
    }
   
    /**
     * Convert User to UserProfileDTO
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
        dto.setDailyRequestsSent(user.getDailyRequestsSent());
        dto.setDailyRequestsAccepted(user.getDailyRequestsAccepted());
       
        // Convert media items
        List<UserMediaDTO> mediaItems = new ArrayList<>();
        // TODO: Fetch actual media items from database
        dto.setMediaItems(mediaItems);
       
        return dto;
    }
   
    /**
     * Update user profile
     */
    @Transactional
    public User updateUserProfile(Long userId, UserProfileDTO updateData) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
       
        if (updateData.getName() != null) {
            user.setName(updateData.getName());
        }
        if (updateData.getProfilePicture() != null) {
            user.setProfilePicture(updateData.getProfilePicture());
        }
        if (updateData.getAccountType() != null) {
            user.setAccountType(updateData.getAccountType());
        }
       
        return userRepository.save(user);
    }
   
    /**
     * Check if user can send requests (daily limit)
     */
    public boolean canSendRequest(User user) {
        int dailyLimit = "PRO".equals(user.getPlanType()) ? 50 : 10;
        return user.getDailyRequestsSent() < dailyLimit;
    }
   
    /**
     * Increment daily request counter
     */
    @Transactional
    public void incrementDailyRequests(User user) {
        user.setDailyRequestsSent(user.getDailyRequestsSent() + 1);
        userRepository.save(user);
    }
   
    /**
     * Reset daily counters (scheduled task)
     */
    @Transactional
    public void resetDailyCounters() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            user.setDailyRequestsSent(0);
            user.setDailyRequestsAccepted(0);
        });
        userRepository.saveAll(users);
        log.info("Reset daily counters for {} users", users.size());
    }
}