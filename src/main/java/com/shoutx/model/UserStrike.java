package com.shoutx.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_strikes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStrike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private StrikeReason reason;

    private String description;
    private Long relatedExchangeId;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum StrikeReason {
        DID_NOT_REPOST, FAKE_REPOST, INAPPROPRIATE_CONTENT, USER_REPORT
    }
}
