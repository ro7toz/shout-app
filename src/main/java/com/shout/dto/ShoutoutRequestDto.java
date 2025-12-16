package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoutoutRequestDto {
    private Long id;
    private String requesterUsername;
    private String targetUsername;
    private String postLink;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private Boolean requesterPosted;
    private Boolean targetPosted;
    private Boolean isExpired;
    private Long hoursRemaining;
}
