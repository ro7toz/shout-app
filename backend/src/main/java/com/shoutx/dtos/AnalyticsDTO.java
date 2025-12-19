package com.shoutx.dtos;

public class AnalyticsDTO {
    private Long totalReach;
    private Long profileVisits;
    private Integer repostsCount;
    private Double completionRate;
    private Long followerGrowth;

    public AnalyticsDTO(Long totalReach, Long profileVisits, Integer repostsCount,
                       Double completionRate, Long followerGrowth) {
        this.totalReach = totalReach;
        this.profileVisits = profileVisits;
        this.repostsCount = repostsCount;
        this.completionRate = completionRate;
        this.followerGrowth = followerGrowth;
    }

    // Getters
    public Long getTotalReach() { return totalReach; }
    public Long getProfileVisits() { return profileVisits; }
    public Integer getRepostsCount() { return repostsCount; }
    public Double getCompletionRate() { return completionRate; }
    public Long getFollowerGrowth() { return followerGrowth; }
}
