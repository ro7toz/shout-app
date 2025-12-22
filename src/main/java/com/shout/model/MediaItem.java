package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * MediaItem Model - User's Instagram media items
 * LOCATION: src/main/java/com/shout/model/MediaItem.java
 */
@Entity
@Table(name = "user_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 500)
    private String url;
    
    @Column(nullable = false, length = 50)
    private String type; // "image", "video", "story", "post", "reel"
    
    @Column(nullable = false, length = 50)
    private String source = "instagram"; // "instagram" or "upload"
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
