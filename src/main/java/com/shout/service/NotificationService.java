package com.shout.service;

import com.shout.exception.ResourceNotFoundException;
import com.shout.model.Notification;
import com.shout.model.User;
import com.shout.repository.NotificationRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Send notification to a user
     */
    @Async
    public void sendNotification(
            String username,
            String type,
            String message,
            String relatedUserId,
            String actionUrl) {

        log.info("Sending notification to {} of type {}", username, type);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .relatedUserId(relatedUserId)
                .actionUrl(actionUrl)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.debug("Notification sent to {}", username);
    }

    /**
     * Get all notifications for a user
     */
    public Page<Notification> getUserNotifications(String username, Pageable pageable) {
        log.debug("Fetching notifications for {}", username);
        return notificationRepository.findByUserUsernameOrderByCreatedAtDesc(username, pageable);
    }

    /**
     * Get unread notification count
     */
    public Long getUnreadCount(String username) {
        return notificationRepository.countUnreadByUsername(username);
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found: " + notificationId));

        if (!notification.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException(
                    "Notification does not belong to user: " + username);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.debug("Marked notification {} as read", notificationId);
    }

    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(String username) {
        log.info("Marking all notifications as read for {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        Page<Notification> notifications = getUserNotifications(username, PageRequest.of(0, Integer.MAX_VALUE));

        for (Notification notification : notifications.getContent()) {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        }

        log.info("Marked all notifications as read for {}", username);
    }

    /**
     * Delete old read notifications (older than 30 days)
     */
    @Async
    public void deleteOldNotifications() {
        log.info("Cleaning up old notifications");

        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Notification> oldNotifications = notificationRepository.findByCreatedAtBeforeAndIsReadTrue(
                    user.getUsername(),
                    threshold
            );

            for (Notification notification : oldNotifications) {
                notificationRepository.delete(notification);
            }

            if (!oldNotifications.isEmpty()) {
                log.debug("Deleted {} old notifications for user {}", oldNotifications.size(), user.getUsername());
            }
        }
    }

    /**
     * Get recent notifications for a user (last 5)
     */
    public Page<Notification> getRecentNotifications(String username) {
        return getUserNotifications(username, PageRequest.of(0, 5));
    }
}
