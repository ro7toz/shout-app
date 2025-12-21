package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ShoutoutRequest Model - FIXED
 * Critical fix: JoinColumn now references 'id' instead of 'username'
 */
@Entity
@Table(name = "shoutout_requests", indexes = {
    @Index(name = "idx_request_status", columnList = "status"),
    @Index(name = "idx_request_requester", columnList = "requester_id"),
    @Index(name = "idx_request_target", columnList = "target_id"),
    @Index(name = "idx_request_accepted_at", columnList = "accepted_at"),
    @Index(name = "idx_request_media_type", columnList = "media_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"requester", "target"})
@EqualsAndHashCode(exclude = {"requester", "target"})
public class ShoutoutRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ FIXED: Now references 'id' instead of 'username'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false)
    private User requester;
    
    // ✅ FIXED: Now references 'id' instead of 'username'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
    private User target;
    
    @Column(name = "post_link")
    private String postLink;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, name = "media_type")
    private MediaType mediaType = MediaType.STORY;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Builder.Default
    @Column(name = "requester_posted", nullable = false)
    private Boolean requesterPosted = false;
    
    @Column(name = "requester_posted_at")
    private LocalDateTime requesterPostedAt;
    
    @Builder.Default
    @Column(name = "target_posted", nullable = false)
    private Boolean targetPosted = false;
    
    @Column(name = "target_posted_at")
    private LocalDateTime targetPostedAt;
    
    @Builder.Default
    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;
    
    @Builder.Default
    @Column(name = "is_notified", nullable = false)
    private Boolean isNotified = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RequestStatus.PENDING;
        }
        if (mediaType == null) {
            mediaType = MediaType.STORY;
        }
    }

    public enum RequestStatus {
        PENDING, 
        ACCEPTED, 
        COMPLETED, 
        FAILED, 
        REJECTED,
        EXPIRED
    }
    
    public enum MediaType {
        STORY,  // Available to BASIC and PRO users
        POST,   // PRO users only
        REEL    // PRO users only
    }
}
