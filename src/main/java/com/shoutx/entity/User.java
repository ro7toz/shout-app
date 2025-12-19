package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_instagram_id", columnList = "instagram_id"),
    @Index(name = "idx_plan_type", columnList = "plan_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    private String username;
    
    private String profilePictureUrl;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType = AccountType.CREATOR;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.BASIC;
    
    private Boolean isVerified = false;
    
    private BigDecimal rating = BigDecimal.ZERO;
    
    private String instagramId;
    
    private String instagramAccessToken;
    
    private String oauthProvider;
    
    private Boolean isActive = true;
    
    private Boolean isBanned = false;
    
    @Column(columnDefinition = "TEXT")
    private String banReason;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserPhoto> photos = new HashSet<>();
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Request> sentRequests = new HashSet<>();
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Request> receivedRequests = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Strike> strikes = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Notification> notifications = new HashSet<>();
    
    public enum AccountType {
        CREATOR, INFLUENCER, BRAND
    }
    
    public enum PlanType {
        BASIC, PRO
    }
}
