package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification model - user notifications
 */
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
 
    @Column(name = "user_id", nullable = false)
    private Long userId;
 
    @Column(nullable = false)
    private String type; // REQUEST_RECEIVED, REQUEST_ACCEPTED, EXCHANGE_COMPLETED, STRIKE_ADDED, etc.
 
    @Column(nullable = false)
    private String title;
 
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
 
    @Column(name = "related_user_id")
    private Long relatedUserId;
 
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }
}