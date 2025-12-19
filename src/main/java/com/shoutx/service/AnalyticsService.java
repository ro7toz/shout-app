package com.shoutx.service;

import com.shoutx.entity.Analytics;
import com.shoutx.entity.User;
import com.shoutx.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final UserService userService;
    
    @Transactional
    public void recordMetrics(Long userId, Long requestId, Integer reach, Integer profileVisits, 
                             Integer clicks, Integer followersGained) {
        User user = userService.getUserById(userId);
        
        // Only Pro users can track analytics
        if (!user.getPlanType().equals(User.PlanType.PRO)) {
            log.warn("User {} attempted to record analytics but is not Pro", userId);
            return;
        }
        
        Analytics analytics = Analytics.builder()
                .user(user)
                .request(null) // Would be fetched from DB
                .reach(reach)
                .profileVisits(profileVisits)
                .clicks(clicks)
                .followersGained(followersGained)
                .metricDate(LocalDate.now())
                .metricType(Analytics.MetricType.DAILY)
                .createdAt(LocalDateTime.now())
                .build();
        
        analyticsRepository.save(analytics);
        log.info("Analytics recorded for user {}", userId);
    }
    
    public AnalyticsSummary getDashboardSummary(Long userId) {
        User user = userService.getUserById(userId);
        
        if (!user.getPlanType().equals(User.PlanType.PRO)) {
            throw new IllegalArgumentException("Only Pro users can access analytics");
        }
        
        Integer totalReach = analyticsRepository.getTotalReachForUser(userId);
        Integer totalVisits = analyticsRepository.getTotalProfileVisitsForUser(userId);
        Integer totalClicks = analyticsRepository.getTotalClicksForUser(userId);
        Integer totalFollowers = analyticsRepository.getTotalFollowersGainedForUser(userId);
        
        return AnalyticsSummary.builder()
                .totalReach(totalReach != null ? totalReach : 0)
                .totalProfileVisits(totalVisits != null ? totalVisits : 0)
                .totalClicks(totalClicks != null ? totalClicks : 0)
                .totalFollowersGained(totalFollowers != null ? totalFollowers : 0)
                .build();
    }
    
    public List<Analytics> getAnalyticsBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.getAnalyticsBetweenDates(userId, startDate, endDate);
    }
    
    @lombok.Data
    @lombok.Builder
    public static class AnalyticsSummary {
        private Integer totalReach;
        private Integer totalProfileVisits;
        private Integer totalClicks;
        private Integer totalFollowersGained;
    }
}
