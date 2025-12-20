package com.shout.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Exchange Detail DTO - Complete exchange information for responses
 * Includes status, users, verification, timer, and action information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeDetailDTO {
   
    // Identifiers
    private Long id;
   
    // Status
    private String status; // COMPLETE, INCOMPLETE, PENDING, ACCEPTED
    private String timeStatus; // LIVE, EXPIRED
   
    // User Information
    private UserMinimalDTO requester;
    private UserMinimalDTO acceptor;
   
    // Media Information
    private MediaDTO requesterMedia;
    private MediaDTO acceptorMedia;
   
    // Verification Status
    private Boolean requesterPosted;
    private Boolean acceptorPosted;
    private LocalDateTime requesterPostedAt;
    private LocalDateTime acceptorPostedAt;
    private String requesterPostUrl;
    private String acceptorPostUrl;
   
    // Timer Information
    private Long hoursRemaining;
    private Long minutesRemaining;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
   
    // Action Indicators
    private Boolean isPendingFromMe;
    private Boolean canRate;
    private Integer myRating; // null if not rated yet
    private String myReview; // Rating review if exists
   
    /**
     * Minimal User DTO - Used within exchange responses
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserMinimalDTO {
        private Long id;
        private String username;
        private String name;
        private String profilePicture;
        private Double rating;
        private Integer strikeCount;
        private String planType;
        private Boolean isVerified;
    }
   
    /**
     * Media DTO - Used within exchange responses
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MediaDTO {
        private Long id;
        private String url;
        private String type; // image, video, story, reel
        private String source; // instagram, upload
        private LocalDateTime createdAt;
    }
}