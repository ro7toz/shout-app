package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String photoUrl;
    
    private String photoKey;
    
    private String caption;
    
    private Boolean isFromInstagram = false;
    
    private String instagramMediaId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
