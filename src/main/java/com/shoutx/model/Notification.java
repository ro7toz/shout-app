package com.shoutx.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String message;
    private String relatedUserId;
    private String relatedExchangeId;

    private Boolean isRead = false;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        SHOUTOUT_REQUEST, SHOUTOUT_ACCEPTED, SHOUTOUT_REPOSTED, PAYMENT_SUCCESSFUL, 
        ACCOUNT_WARNING, ACCOUNT_BANNED
    }
}
