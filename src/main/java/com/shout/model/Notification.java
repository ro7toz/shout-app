package com.shout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_read", columnList = "user_id, is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "username")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    private String title;
    private String message;
    private String actionUrl;
    
    private Boolean isRead = false;
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "related_request_id")
    private ShoutoutRequest relatedRequest;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        REQUEST_RECEIVED, REQUEST_ACCEPTED, REQUEST_EXPIRED, BAD_RATING, ADDED_TO_CIRCLE
    }
}
