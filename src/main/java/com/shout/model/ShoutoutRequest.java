package com.shout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "shoutout_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoutoutRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    @Column(columnDefinition = "TEXT")
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

    public enum RequestStatus {
        PENDING, ACCEPTED, COMPLETED, FAILED, EXPIRED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }

    public boolean isWithin24Hours() {
        if (acceptedAt == null) return false;
        return LocalDateTime.now().isBefore(acceptedAt.plusHours(24));
    }

    public boolean isExpiredByDeadline() {
        if (acceptedAt == null) return false;
        return LocalDateTime.now().isAfter(acceptedAt.plusHours(24));
    }

    public long getHoursRemaining() {
        if (acceptedAt == null) return 0;
        return 24 - java.time.temporal.ChronoUnit.HOURS.between(acceptedAt, LocalDateTime.now());
    }
}