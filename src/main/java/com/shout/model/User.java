package com.shout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_category", columnList = "category"),
    @Index(name = "idx_user_rating", columnList = "average_rating DESC"),
    @Index(name = "idx_user_active", columnList = "is_active")
})
@Data
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
    
    private Double averageRating = 5.0;
    private Integer totalRatings = 0;
    
    private String accessToken;
    private LocalDateTime tokenExpiresAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
