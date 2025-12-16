package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shoutout_requests", indexes = {
    @Index(name = "idx_request_status", columnList = "status"),
    @Index(name = "idx_request_requester", columnList = "requester_id"),
    @Index(name = "idx_request_target", columnList = "target_id"),
    @Index(name = "idx_request_accepted_at", columnList = "accepted_at")
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "username")
    private User requester;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "username")
    private User target;
    
    private String postLink;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    
    @Builder.Default
    private Boolean requesterPosted = false;
    private LocalDateTime requesterPostedAt;
    
    @Builder.Default
    private Boolean targetPosted = false;
    private LocalDateTime targetPostedAt;
    
    @Builder.Default
    private Boolean isExpired = false;
    
    @Builder.Default
    private Boolean isNotified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }

    public enum RequestStatus {
        PENDING, ACCEPTED, COMPLETED, FAILED, REJECTED
    }
}
