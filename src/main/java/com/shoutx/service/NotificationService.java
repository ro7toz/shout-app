package com.shoutx.service;

import com.shoutx.entity.Notification;
import com.shoutx.entity.Request;
import com.shoutx.entity.User;
import com.shoutx.entity.Payment;
import com.shoutx.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    
    @Transactional
    public void sendNewRequestNotification(User receiver, User sender, Request request) {
        // In-app notification
        Notification notification = Notification.builder()
                .user(receiver)
                .notificationType(Notification.NotificationType.NEW_REQUEST)
                .title(sender.getName() + " sent a shoutout request")
                .message(sender.getName() + " sent you a shoutout request. Check your dashboard to accept.")
                .relatedUser(sender)
                .relatedRequest(request)
                .isRead(false)
                .actionUrl("/dashboard")
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        
        // Email notification
        emailService.sendNewRequestEmail(receiver, sender);
        log.info("New request notification sent to user {}", receiver.getId());
    }
    
    @Transactional
    public void sendCompletionNotification(User sender, User receiver, Request request) {
        // Send to both users
        Notification notificationSender = Notification.builder()
                .user(sender)
                .notificationType(Notification.NotificationType.REQUEST_COMPLETED)
                .title("Shoutout exchange completed")
                .message(receiver.getName() + " completed the shoutout exchange.")
                .relatedUser(receiver)
                .relatedRequest(request)
                .isRead(false)
                .actionUrl("/dashboard")
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification notificationReceiver = Notification.builder()
                .user(receiver)
                .notificationType(Notification.NotificationType.REQUEST_COMPLETED)
                .title("Shoutout exchange completed")
                .message(sender.getName() + " completed the shoutout exchange.")
                .relatedUser(sender)
                .relatedRequest(request)
                .isRead(false)
                .actionUrl("/dashboard")
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notificationSender);
        notificationRepository.save(notificationReceiver);
        
        emailService.sendCompletionEmail(sender, receiver);
        log.info("Completion notifications sent for request {}", request.getId());
    }
    
    @Transactional
    public void sendStrikeWarningNotification(User user, Integer strikeCount) {
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(Notification.NotificationType.STRIKE_WARNING)
                .title("Strike warning: " + strikeCount + "/3")
                .message("You have " + strikeCount + " strikes. One more strike will result in account ban.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        emailService.sendStrikeWarningEmail(user, strikeCount);
        log.warn("Strike warning sent to user {}: {}/3", user.getId(), strikeCount);
    }
    
    @Transactional
    public void sendAccountBannedNotification(User user) {
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(Notification.NotificationType.ACCOUNT_BANNED)
                .title("Your account has been suspended")
                .message("Your account has been suspended due to multiple violations. Contact support for details.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        emailService.sendBanNotificationEmail(user);
        log.warn("Account ban notification sent to user {}", user.getId());
    }
    
    @Transactional
    public void sendPaymentSuccessNotification(User user, Payment payment) {
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(Notification.NotificationType.PRO_UPGRADE_SUCCESS)
                .title("Pro plan activated")
                .message("Congratulations! Your Pro plan is now active.")
                .isRead(false)
                .actionUrl("/dashboard")
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        emailService.sendPaymentSuccessEmail(user, payment);
        log.info("Payment success notification sent to user {}", user.getId());
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.getUserNotificationsOrderByNewest(userId);
    }
    
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadNotificationsForUser(userId);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
