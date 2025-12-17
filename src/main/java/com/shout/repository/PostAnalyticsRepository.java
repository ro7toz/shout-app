package com.shout.repository;

import com.shout.model.PostAnalytics;
import com.shout.model.ShoutoutExchange;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {
    List<PostAnalytics> findByUser(User user);
    Optional<PostAnalytics> findByShoutoutExchange(ShoutoutExchange exchange);
    List<PostAnalytics> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(pa.impressions) FROM PostAnalytics pa WHERE pa.user = :user")
    Long getTotalImpressions(User user);
    
    @Query("SELECT SUM(pa.clicks) FROM PostAnalytics pa WHERE pa.user = :user")
    Long getTotalClicks(User user);
    
    @Query("SELECT SUM(pa.profileVisits) FROM PostAnalytics pa WHERE pa.user = :user")
    Long getTotalProfileVisits(User user);
    
    @Query("SELECT AVG(pa.engagementRate) FROM PostAnalytics pa WHERE pa.user = :user")
    Double getAverageEngagementRate(User user);
}
