package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_is_read", columnList = "is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id")
    private User relatedUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_request_id")
    private Request relatedRequest;
    
    private Boolean isRead = false;
    
    private String actionUrl;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum NotificationType {
        NEW_REQUEST,
        REQUEST_ACCEPTED,
        REQUEST_COMPLETED,
        REPOST_REMINDER,
        STRIKE_WARNING,
        PRO_UPGRADE_SUCCESS,
        PAYMENT_FAILED,
        ACCOUNT_BANNED
    }
}
