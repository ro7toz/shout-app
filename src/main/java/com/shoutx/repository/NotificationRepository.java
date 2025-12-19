package com.shoutx.repository;

import com.shoutx.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserId(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> getUserNotificationsOrderByNewest(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadNotificationsForUser(@Param("userId") Long userId);
    
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.notificationType = :type ORDER BY n.createdAt DESC")
    List<Notification> getNotificationsByType(@Param("userId") Long userId, 
                                               @Param("type") Notification.NotificationType type);
}
