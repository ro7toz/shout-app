package com.shout.repository;

import com.shout.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Repository - Extended
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
   
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
   
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
   
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
   
    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime cutoffDate);
}