package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoutout_exchange_id", nullable = false)
    private ShoutoutExchange shoutoutExchange;

    private String instagramPostId; // Reference to Instagram post/story/reel
    private String instagramMediaUrl; // URL of the post

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType; // STORY, POST, REEL

    // Analytics data fetched from Instagram
    private Long impressions = 0L; // Total reach/views
    private Long clicks = 0L; // Link clicks
    private Long profileVisits = 0L; // Profile visits attributed to this shout
    private Long shares = 0L;
    private Long saves = 0L;
    private Long comments = 0L;
    private Long likes = 0L;

    // Engagement rate
    private Double engagementRate = 0.0;

    private Boolean analyticsVerified = false; // Whether we fetched from Instagram API

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastFetchedAt; // When analytics were last fetched

    public enum MediaType {
        STORY,
        POST,
        REEL
    }
}
