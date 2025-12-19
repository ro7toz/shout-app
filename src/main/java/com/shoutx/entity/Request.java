package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", indexes = {
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_receiver_id", columnList = "receiver_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private UserPhoto photo;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private RepostStatus senderRepostStatus;
    
    @Enumerated(EnumType.STRING)
    private RepostStatus receiverRepostStatus;
    
    @Column(nullable = false)
    private LocalDateTime deadline;
    
    private LocalDateTime repostCompletedAt;
    
    private Integer ratingBySender;
    
    private Integer ratingByReceiver;
    
    @Column(columnDefinition = "TEXT")
    private String senderComment;
    
    @Column(columnDefinition = "TEXT")
    private String receiverComment;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum RequestStatus {
        PENDING, ACCEPTED, COMPLETED, EXPIRED, CANCELLED
    }
    
    public enum RepostStatus {
        PENDING, REPOSTED, FAILED
    }
}
