package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationType violationType; // FAILED_TO_POST, REMOVED_POST, PROMISED_AND_DIDNT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    private ShoutoutExchange exchange; // Related exchange

    @Column(nullable = false)
    private Integer strikeNumber; // 1, 2, or 3

    @Column(columnDefinition = "TEXT")
    private String description; // Details of the violation

    private Boolean accountBanned = false; // True if strikes >= 3
    private LocalDateTime bannedAt; // When account was banned
    private Boolean socialLoginBanned = false; // Ban this social login from future signups

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ViolationType {
        FAILED_TO_POST,     // Didn't post within 24 hours
        REMOVED_POST,       // Posted but removed later
        PROMISED_AND_DIDNT, // Promised to post but didn't
        MULTIPLE_VIOLATIONS // Multiple rule violations
    }
}
