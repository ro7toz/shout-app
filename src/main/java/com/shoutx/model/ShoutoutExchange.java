package com.shoutx.model;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "sender_media_id")
    private UserMedia senderMedia;

    @ManyToOne
    @JoinColumn(name = "receiver_media_id")
    private UserMedia receiverMedia;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus status = ExchangeStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private CompletionStatus completionStatus = CompletionStatus.INCOMPLETE;

    private Boolean senderReposted = false;
    private Boolean receiverReposted = false;

    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime senderRepostAt;
    private LocalDateTime receiverRepostAt;
    private LocalDateTime expiresAt;

    private Integer senderRating;
    private String senderReview;
    private Integer receiverRating;
    private String receiverReview;

    private Long reachCount = 0L;
    private Long profileVisits = 0L;
    private Long linkClicks = 0L;
    private Long followerGain = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusHours(24);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void updateCompletionStatus() {
        if (senderReposted && receiverReposted) {
            this.completionStatus = CompletionStatus.COMPLETE;
        } else if (isExpired()) {
            this.completionStatus = CompletionStatus.EXPIRED;
        }
    }

    public enum ExchangeStatus {
        PENDING, ACCEPTED, REJECTED, COMPLETED
    }

    public enum CompletionStatus {
        INCOMPLETE, COMPLETE, EXPIRED
    }
}
