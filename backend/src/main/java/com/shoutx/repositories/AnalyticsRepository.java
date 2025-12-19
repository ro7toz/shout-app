package com.shoutx.repositories;

import com.shoutx.models.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Analytics findByUserId(Long userId);
    
    @Query("SELECT AVG(u.rating) FROM User u WHERE u.id = ?1")
    Double getAverageRating(Long userId);
    
    @Query("SELECT COUNT(u.followers) FROM User u WHERE u.id = ?1 AND u.updatedAt >= DATE_SUB(NOW(), INTERVAL 1 MONTH)")
    Long getMonthlyFollowerGrowth(Long userId);
}
