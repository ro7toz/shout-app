package com.shoutx.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private String caption;
    private LocalDateTime uploadedAt;
    private Boolean isFromInstagram = false;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public enum MediaType {
        STORY, POST, REEL
    }
}
