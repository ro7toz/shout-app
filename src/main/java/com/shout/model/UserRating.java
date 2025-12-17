package com.shout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rater_id", "ratee_id", "exchange_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater; // User giving the rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ratee_id", nullable = false)
    private User ratee; // User being rated

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    private ShoutoutExchange exchange; // The exchange being rated

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String review; // Optional review text

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingCategory category; // RELIABILITY, CONTENT_QUALITY, PROFESSIONALISM

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum RatingCategory {
        RELIABILITY,        // Did they post on time?
        CONTENT_QUALITY,    // Quality of content shared
        PROFESSIONALISM,    // Professional behavior
        ENGAGEMENT          // Engagement and response
    }
}
