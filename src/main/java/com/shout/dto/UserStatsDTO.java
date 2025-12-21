package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private String username;
    private Long totalRequestsSent;
    private Long totalRequestsReceived;
    private Long completedRequests;
    private Long failedRequests;
    private Double completionRate;
    private Long circleSize;
    private Double averageRating;
    private Long totalRatings;
}
