package com.shout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "shoutout_requests", indexes = {
    @Index(name = "idx_request_status", columnList = "status"),
    @Index(name = "idx_request_requester", columnList = "requester_id"),
    @Index(name = "idx_request_target", columnList = "target_id"),
    @Index(name = "idx_request_accepted_at", columnList = "accepted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoutoutRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "username")
    private User requester;
    
    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "username")
    private User target;
    
    private String postLink;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    
    private Boolean requesterPosted = false;
    private LocalDateTime requesterPostedAt;
    
    private Boolean targetPosted = false;
    private LocalDateTime targetPostedAt;
    
    private Boolean isExpired = false;
    private Boolean isNotified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = RequestStatus.PENDING;
    }

    public enum RequestStatus {
        PENDING, ACCEPTED, COMPLETED, FAILED, REJECTED
    }
}
