package com.shout.service;

import com.shout.dto.NotificationDto;
import com.shout.model.Notification;
import com.shout.model.ShoutoutRequest;
import com.shout.model.User;
import com.shout.repository.NotificationRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Async
    public void notifyRequestReceived(User target, User requester, ShoutoutRequest request) {
        Notification notification = Notification.builder()
            .user(target)
            .type(Notification.NotificationType.REQUEST_RECEIVED)
            .title("New Shoutout Request")
            .message("@" + requester.getUsername() + " sent you a shoutout request")
            .actionUrl("/dashboard?tab=pending")
            .relatedRequest(request)
            .isRead(false)
            .build();
        
        notificationRepository.save(notification);
        log.info("Notification sent to {}", target.getUsername());
    }

    @Async
    public void notifyRequestAccepted(User requester, ShoutoutRequest request) {
        Notification notification = Notification.builder()
            .user(requester)
            .type(Notification.NotificationType.REQUEST_ACCEPTED)
            .title("Request Accepted!")
            .message("@" + request.getTarget().getUsername() + " accepted your shoutout request. You have 24 hours to post!")
            .actionUrl("/dashboard?tab=sent")
            .relatedRequest(request)
            .isRead(false)
            .build();
        
        notificationRepository.save(notification);
    }

    @Async
    public void notifyRequestExpired(ShoutoutRequest request) {
        // Notify both users
        Notification requesterNotif = Notification.builder()
            .user(request.getRequester())
            .type(Notification.NotificationType.REQUEST_EXPIRED)
            .title("Request Expired")
            .message("Your shoutout request with @" + request.getTarget().getUsername() + " has expired")
            .actionUrl("/dashboard")
            .relatedRequest(request)
            .isRead(false)
            .build();
        
        Notification targetNotif = Notification.builder()
            .user(request.getTarget())
            .type(Notification.NotificationType.REQUEST_EXPIRED)
            .title("Request Expired")
            .message("Your shoutout request with @" + request.getRequester().getUsername() + " has expired")
            .actionUrl("/dashboard")
            .relatedRequest(request)
            .isRead(false)
            .build();
        
        notificationRepository.save(requesterNotif);
        notificationRepository.save(targetNotif);
    }

    @Async
    public void notifyBadRating(User ratedUser, int score) {
        if (score <= 2) {
            Notification notification = Notification.builder()
                .user(ratedUser)
                .type(Notification.NotificationType.BAD_RATING)
                .title("Low Rating Received")
                .message("You received a " + score + "-star rating")
                .actionUrl("/profile")
                .isRead(false)
                .build();
            
            notificationRepository.save(notification);
        }
    }

    @Async
    public void notifyAddedToCircle(User user, User friend) {
        Notification notification = Notification.builder()
            .user(user)
            .type(Notification.NotificationType.ADDED_TO_CIRCLE)
            .title("Added to Circle!")
            .message("@" + friend.getUsername() + " was added to your circle")
            .actionUrl("/dashboard?tab=circle")
            .isRead(false)
            .build();
        
        notificationRepository.save(notification);
    }

    public Page<NotificationDto> getUserNotifications(String username, Pageable pageable) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return notifications.map(this::convertToDto);
    }

    public Long getUnreadCount(String username) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String username) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            notificationRepository.deleteOldNotifications(user, cutoffDate);
        }
        
        log.info("Old notifications cleaned up");
    }

    private NotificationDto convertToDto(Notification notification) {
        String timeAgo = getTimeAgo(notification.getCreatedAt());
        
        return NotificationDto.builder()
            .id(notification.getId())
            .type(notification.getType().toString())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .actionUrl(notification.getActionUrl())
            .isRead(notification.getIsRead())
            .createdAt(notification.getCreatedAt())
            .timeAgo(timeAgo)
            .build();
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24) return hours + "h ago";
        return days + "d ago";
    }
}
