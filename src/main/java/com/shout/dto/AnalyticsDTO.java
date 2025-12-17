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
public class AnalyticsDTO {
    private Long exchangeId;
    private String mediaType;
    private Long impressions;
    private Long clicks;
    private Long profileVisits;
    private Long shares;
    private Long saves;
    private Long comments;
    private Long likes;
    private Double engagementRate;
    private LocalDateTime createdAt;
}
