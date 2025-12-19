package com.shoutx.dtos;

public class AnalyticsDTO {
    private Long totalReach;
    private Long profileVisits;
    private Integer repostsCount;
    private Double completionRate;
    private Long followerGrowth;

    public AnalyticsDTO(Long reach, Long visits, Integer reposts, Double rate, Long growth) {
        this.totalReach = reach;
        this.profileVisits = visits;
        this.repostsCount = reposts;
        this.completionRate = rate;
        this.followerGrowth = growth;
    }

    public Long getTotalReach() { return totalReach; }
    public Long getProfileVisits() { return profileVisits; }
    public Integer getRepostsCount() { return repostsCount; }
    public Double getCompletionRate() { return completionRate; }
    public Long getFollowerGrowth() { return followerGrowth; }
}