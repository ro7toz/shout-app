package com.shoutx.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @Column(nullable = false)
    private Long totalReach = 0L;
    
    @Column(nullable = false)
    private Long profileVisits = 0L;
    
    @Column(nullable = false)
    private Integer repostsCount = 0;
    
    @Column(nullable = false)
    private Double completionRate = 0.0;
    
    @Column(nullable = false)
    private Long followerGrowth = 0L;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getTotalReach() { return totalReach; }
    public void setTotalReach(Long totalReach) { this.totalReach = totalReach; }
    
    public Long getProfileVisits() { return profileVisits; }
    public void setProfileVisits(Long profileVisits) { this.profileVisits = profileVisits; }
    
    public Integer getRepostsCount() { return repostsCount; }
    public void setRepostsCount(Integer repostsCount) { this.repostsCount = repostsCount; }
    
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    
    public Long getFollowerGrowth() { return followerGrowth; }
    public void setFollowerGrowth(Long followerGrowth) { this.followerGrowth = followerGrowth; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
