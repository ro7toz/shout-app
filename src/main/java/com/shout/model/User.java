package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Model - Complete implementation with all required fields
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Basic Info
    @Column(unique = true, nullable = false)
    private String email;
 
    @Column(nullable = false)
    private String name;
 
    @Column(unique = true)
    private String username;
 
    @Column(name = "profile_picture")
    private String profilePicture;
 
    @Column(columnDefinition = "TEXT")
    private String bio;
 
    // Instagram Integration
    @Column(name = "instagram_id")
    private String instagramId;
 
    @Column(name = "instagram_username")
    private String instagramUsername;
 
    @Column(name = "instagram_access_token", columnDefinition = "TEXT")
    private String instagramAccessToken;
 
    // Facebook Integration
    @Column(name = "facebook_id", unique = true)
    private String facebookId;
 
    @Column(name = "facebook_access_token", columnDefinition = "TEXT")
    private String facebookAccessToken;
 
    @Column(name = "facebook_token_expires_at")
    private Long facebookTokenExpiresAt;
 
    // Account Details
    @Column(name = "account_type")
    private String accountType = "Creator";
 
    @Column(name = "plan_type")
    private String planType = "BASIC";
 
    @Column(name = "is_verified")
    private Boolean isVerified = false;
 
    private Integer followers = 0;
 
    private String category;
 
    // Ratings
    @Column(nullable = false)
    private Double rating = 0.0;
 
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;
 
    // Compliance & Strike System
    @Builder.Default
    @Column(name = "strike_count", nullable = false)
    private Integer strikeCount = 0;
 
    @Builder.Default
    @Column(name = "account_banned", nullable = false)
    private Boolean accountBanned = false;
 
    @Builder.Default
    @Column(name = "social_login_banned", nullable = false)
    private Boolean socialLoginBanned = false;
 
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;
 
    // Daily Limits
    @Column(name = "daily_requests_sent")
    private Integer dailyRequestsSent = 0;
 
    @Column(name = "daily_requests_accepted")
    private Integer dailyRequestsAccepted = 0;
 
    // Subscription
    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;
 
    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;
 
    // Status
    @Column(name = "is_active")
    private Boolean isActive = true;
 
    // Timestamps
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
 
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMedia> mediaItems = new ArrayList<>();
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 
    // Helper methods
    public boolean isPro() {
        return "PRO".equalsIgnoreCase(planType);
    }
 
    public boolean isBanned() {
        return accountBanned != null && accountBanned;
    }
 
    public boolean hasMaxStrikes() {
        return strikeCount != null && strikeCount >= 3;
    }
}