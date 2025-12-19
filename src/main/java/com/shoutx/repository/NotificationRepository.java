package com.shoutx.repository;

import com.shoutx.model.Notification;
import com.shoutx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotifications(User user);

    long countByUserAndIsReadFalse(User user);
}
