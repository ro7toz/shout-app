package com.shoutx.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    private String profileImageUrl;
    private String instagramId;

    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.BASIC;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Boolean isInstagramVerified = false;
    private Double rating = 0.0;
    private Integer totalRatings = 0;
    private Long followerCount = 0L;

    // Strike system for non-compliance
    private Integer strikeCount = 0;
    private Boolean isAccountBanned = false;
    private Boolean isAccountDeleted = false;

    // Daily request limits tracking
    private Integer dailySendRequestsCount = 0;
    private Integer dailyAcceptRequestsCount = 0;
    private LocalDateTime lastRequestResetTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserMedia> mediaFiles;

    @OneToMany(mappedBy = "sender")
    private List<ShoutoutExchange> sentExchanges;

    @OneToMany(mappedBy = "receiver")
    private List<ShoutoutExchange> receivedExchanges;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserStrike> strikes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean canSendRequest() {
        resetDailyLimitsIfNeeded();
        int limit = planType == PlanType.PRO ? 50 : 10;
        return dailySendRequestsCount < limit && !isAccountBanned;
    }

    public boolean canAcceptRequest() {
        resetDailyLimitsIfNeeded();
        int limit = planType == PlanType.PRO ? 50 : 10;
        return dailyAcceptRequestsCount < limit && !isAccountBanned;
    }

    private void resetDailyLimitsIfNeeded() {
        if (lastRequestResetTime == null || 
            LocalDateTime.now().getDayOfYear() > lastRequestResetTime.getDayOfYear()) {
            dailySendRequestsCount = 0;
            dailyAcceptRequestsCount = 0;
            lastRequestResetTime = LocalDateTime.now();
        }
    }

    public void addStrike() {
        this.strikeCount++;
        if (this.strikeCount >= 3) {
            this.isAccountBanned = true;
            this.isAccountDeleted = true;
        }
    }

    public enum PlanType {
        BASIC, PRO
    }

    public enum AccountType {
        INSTAGRAM, INFLUENCER, MICRO_INFLUENCER
    }
}
