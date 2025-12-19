package com.shoutx.controllers;

import com.shoutx.models.Notification;
import com.shoutx.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pages/notifications")
public class NotificationsPageController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        List<Notification> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(new Object() {
            public long getUnreadCount() { return unreadCount; }
        });
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
        @PathVariable Long notificationId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        Notification notification = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
        @PathVariable Long notificationId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok("Notification deleted");
    }
}
