package com.shout.service;

import com.shout.model.Notification;
import com.shout.model.User;
import com.shout.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Notification Service - handles user notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
   
    private final NotificationRepository notificationRepository;
   
    /**
     * Get user notifications
     */
    public List<Notification> getUserNotifications(Long userId, int page, int pageSize) {
        // TODO: Implement pagination
        return new ArrayList<>();
    }
   
    /**
     * Get notifications by type
     */
    public List<Notification> getNotificationsByType(Long userId, String type, int page, int pageSize) {
        // TODO: Implement filtering
        return new ArrayList<>();
    }
   
    /**
     * Get unread notification count
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
   
    /**
     * Get notification by ID
     */
    public Optional<Notification> getNotification(Long id) {
        return notificationRepository.findById(id);
    }
   
    /**
     * Save notification
     */
    @Transactional
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
   
    /**
     * Delete notification
     */
    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
   
    /**
     * Mark all as read
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsRead(userId, false);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
   
    /**
     * Create notification
     */
    @Transactional
    public Notification createNotification(User user, String title, String message, String actionUrl) {
        Notification notification = Notification.builder()
            .userId(user.getId())
            .type("GENERAL")
            .title(title)
            .message(message)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
       
        return notificationRepository.save(notification);
    }
}