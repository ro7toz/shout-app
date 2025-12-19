package com.shoutx.service;

import com.shoutx.entity.User;
import com.shoutx.repository.UserRepository;
import com.shoutx.repository.StrikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final StrikeRepository strikeRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User registerUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .planType(User.PlanType.BASIC)
                .isActive(true)
                .isBanned(false)
                .isVerified(false)
                .rating(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
    
    public User registerOrUpdateOAuthUser(String instagramId, String name, String profilePictureUrl, 
                                          String username, String accessToken) {
        Optional<User> existingUser = userRepository.findByInstagramId(instagramId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setProfilePictureUrl(profilePictureUrl);
            user.setUsername(username);
            user.setInstagramAccessToken(accessToken);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        
        User newUser = User.builder()
                .instagramId(instagramId)
                .name(name)
                .profilePictureUrl(profilePictureUrl)
                .username(username)
                .instagramAccessToken(accessToken)
                .oauthProvider("INSTAGRAM")
                .email(instagramId + "@instagram.com")
                .planType(User.PlanType.BASIC)
                .isActive(true)
                .isBanned(false)
                .isVerified(false)
                .rating(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(newUser);
    }
    
    @Transactional
    public User updateProfile(Long userId, String name, String bio, User.AccountType accountType) {
        User user = getUserById(userId);
        user.setName(name);
        user.setBio(bio);
        user.setAccountType(accountType);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public void updatePlan(Long userId, User.PlanType planType) {
        User user = getUserById(userId);
        user.setPlanType(planType);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User {} plan upgraded to {}", userId, planType);
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByInstagramId(String instagramId) {
        return userRepository.findByInstagramId(instagramId);
    }
    
    public List<User> searchUsers(User.AccountType genre) {
        return userRepository.searchUsers(genre);
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrueAndIsBannedFalse();
    }
    
    public List<User> getTopRatedUsers(int limit) {
        return userRepository.getTopRatedUsers(limit);
    }
    
    @Transactional
    public void banUser(Long userId, String reason) {
        User user = getUserById(userId);
        user.setIsBanned(true);
        user.setBanReason(reason);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.warn("User {} banned for reason: {}", userId, reason);
    }
    
    @Transactional
    public void unbanUser(Long userId) {
        User user = getUserById(userId);
        user.setIsBanned(false);
        user.setBanReason(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User {} unbanned", userId);
    }
    
    public Boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public Long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    @Transactional
    public void checkAndBanIfNeeded(Long userId) {
        Integer strikeCount = strikeRepository.getStrikeCountForUser(userId);
        if (strikeCount >= 3) {
            banUser(userId, "Account banned due to 3 strikes");
        }
    }
    
    public User updateRating(Long userId, Double newRating) {
        User user = getUserById(userId);
        user.setRating(new BigDecimal(newRating).setScale(2, java.math.RoundingMode.HALF_UP));
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
