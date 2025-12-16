package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String fullName;
    private String profilePicUrl;
    private String category;
    private Integer followerCount;
    private String biography;
    private String websiteUrl;
    private Double averageRating;
    private Long totalRatings;
    private Long pendingRequestsCount;
    private Long sentRequestsCount;
    private Long circleSize;
}
