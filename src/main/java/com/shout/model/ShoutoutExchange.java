package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shoutout_exchanges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoutoutExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester; // User requesting the shoutout

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptor_id", nullable = false)
    private User acceptor; // User accepting the request

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ShoutoutRequest shoutoutRequest; // Original request

    // Post/Story/Reel details being exchanged
    private String postUrl; // Link to the post
    private String postCaption;
    private String hashtags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType; // STORY, POST, REEL

    // 24-hour timer window
    @Column(nullable = false)
    private LocalDateTime expiresAt; // When the 24hr window closes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status; // PENDING, COMPLETED, FAILED, EXPIRED

    // Requester's action
    private LocalDateTime requesterPostedAt; // When requester posted
    private Boolean requesterPosted = false;
    private String requesterPostUrl; // URL of posted content

    // Acceptor's action
    private LocalDateTime acceptorPostedAt; // When acceptor posted
    private Boolean acceptorPosted = false;
    private String acceptorPostUrl; // URL of posted content

    // Compliance
    private Boolean requesterRemoved = false; // If requester removed after posting
    private Boolean acceptorRemoved = false; // If acceptor removed after posting
    private LocalDateTime requesterRemovedAt;
    private LocalDateTime acceptorRemovedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ExchangeStatus {
        PENDING,        // Waiting for both to post
        COMPLETED,      // Both posted successfully
        FAILED,         // One or both didn't post within 24hr
        EXPIRED,        // 24hr window passed
        CANCELLED       // Manually cancelled
    }

    public enum MediaType {
        STORY,
        POST,
        REEL
    }
}
