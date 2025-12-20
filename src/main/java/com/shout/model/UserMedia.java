package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * UserMedia Model - stores user's media items
 */
@Entity
@Table(name = "user_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMedia {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    @Column(nullable = false)
    private String url;
 
    @Column(nullable = false)
    private String type; // image, video, story
 
    @Column(nullable = false)
    private String source = "instagram"; // instagram, upload
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}