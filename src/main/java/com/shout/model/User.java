package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_category", columnList = "category"),
    @Index(name = "idx_user_rating", columnList = "average_rating DESC"),
    @Index(name = "idx_user_active", columnList = "is_active"),
    @Index(name = "idx_user_facebook_id", columnList = "facebook_id"),
    @Index(name = "idx_user_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String username;
    
    private String instagramId;
    private String fullName;
    private String profilePicUrl;
    private String category;
    private Integer followerCount;
    private String biography;
    private String websiteUrl;
    private String accountType;
    
    @Builder.Default
    private Double averageRating = 5.0;
    
    @Builder.Default
    private Integer totalRatings = 0;
    
    private String accessToken;
    private LocalDateTime tokenExpiresAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    
    @Builder.Default
    private Boolean isActive = true;

    // ===== FACEBOOK LOGIN FIELDS =====
    @Column(unique = true, nullable = true)
    private String facebookId;              // Facebook user ID
    
    private String email;                   // Email from Facebook
    
    @Column(name = "facebook_access_token")
    private String facebookAccessToken;     // Facebook OAuth token
    
    @Column(name = "facebook_token_expires_at")
    private Long facebookTokenExpiresAt;    // When Facebook token expires (Unix timestamp)
    
    @Column(name = "name")
    private String name;                    // User's name from Facebook
    
    @Column(name = "profile_picture")
    private String profilePicture;          // Profile picture URL from Facebook
    
    @Column(name = "bio")
    private String bio;                     // User's bio
    
    // ===== END FACEBOOK FIELDS =====

    // ===== SUBSCRIPTION & PAYMENT FIELDS =====
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.BASIC; // BASIC or PRO

    @Builder.Default
    @Column(nullable = false)
    private Integer strikeCount = 0; // 0-3 strikes

    @Builder.Default
    @Column(nullable = false)
    private Boolean accountBanned = false; // True if strikes >= 3

    @Builder.Default
    @Column(nullable = false)
    private Boolean socialLoginBanned = false; // Ban this social account

    private LocalDateTime bannedAt; // When account was banned

    // ===== END SUBSCRIPTION FIELDS =====

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }

    public enum SubscriptionStatus {
        BASIC,
        PRO
    }
}
