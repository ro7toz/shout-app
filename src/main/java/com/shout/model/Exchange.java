package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Exchange model - represents shoutout exchange between two users
 */
@Entity
@Table(name = "exchanges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoutout_request_id", nullable = false)
    private ShoutoutRequest shoutoutRequest;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
 
    @Column(name = "sender_media_url")
    private String senderMediaUrl;
 
    @Column(name = "recipient_media_url")
    private String recipientMediaUrl;
 
    @Column(nullable = false)
    private String status; // ACCEPTED, PENDING, COMPLETED, INCOMPLETE, EXPIRED
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;
 
    @Column(name = "exchanged_at")
    private LocalDateTime exchangedAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (deadline == null) {
            deadline = createdAt.plusHours(24);
        }
    }
}