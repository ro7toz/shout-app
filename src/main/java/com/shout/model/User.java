package com.shout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

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

    @OneToMany(mappedBy = "ratedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> receivedRatings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_circle",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> circle = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }

    public void updateAverageRating() {
        if (receivedRatings.isEmpty()) {
            this.averageRating = 5.0;
            this.totalRatings = 0;
        } else {
            double avg = receivedRatings.stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(5.0);
            this.averageRating = Math.round(avg * 10.0) / 10.0;
            this.totalRatings = receivedRatings.size();
        }
    }
}