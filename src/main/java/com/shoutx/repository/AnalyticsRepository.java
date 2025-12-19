package com.shoutx.repository;

import com.shoutx.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    
    List<Analytics> findByUserId(Long userId);
    
    @Query("SELECT a FROM Analytics a WHERE a.user.id = :userId AND a.metricDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.metricDate ASC")
    List<Analytics> getAnalyticsBetweenDates(@Param("userId") Long userId, 
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(a.reach) FROM Analytics a WHERE a.user.id = :userId")
    Integer getTotalReachForUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(a.profileVisits) FROM Analytics a WHERE a.user.id = :userId")
    Integer getTotalProfileVisitsForUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(a.clicks) FROM Analytics a WHERE a.user.id = :userId")
    Integer getTotalClicksForUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(a.followersGained) FROM Analytics a WHERE a.user.id = :userId")
    Integer getTotalFollowersGainedForUser(@Param("userId") Long userId);
    
    List<Analytics> findByRequestId(Long requestId);
}
