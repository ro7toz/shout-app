package com.shoutx.services;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public Object createNotification(Object notification) {
        return notification;
    }
    public long getUnreadCount(Long userId) {
        return 5L;
    }
    public Object markAsRead(Long notificationId) {
        return new Object() {
            public Long getId() { return notificationId; }
        };
    }
}