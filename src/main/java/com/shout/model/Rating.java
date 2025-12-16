package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", indexes = {
    @Index(name = "idx_rating_rated_user", columnList = "rated_user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"rater", "ratedUser", "relatedRequest"})
@EqualsAndHashCode(exclude = {"rater", "ratedUser", "relatedRequest"})
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", referencedColumnName = "username")
    private User rater;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", referencedColumnName = "username")
    private User ratedUser;
    
    @Column(nullable = false)
    private Integer score;
    
    private String reason;
    private String comment;
    
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_request_id")
    private ShoutoutRequest relatedRequest;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
