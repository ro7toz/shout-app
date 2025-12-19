package com.shoutx.pages;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.*;

@RestController
@RequestMapping("/api/pages/notifications")
public class NotificationsPageController {
    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication auth) {
        return ResponseEntity.ok(Arrays.asList(
            new Object() {
                public Long getId() { return 1L; }
                public String getTitle() { return "New Request"; }
                public String getMessage() { return "Someone sent you a request"; }
                public Boolean getIsRead() { return false; }
            }
        ));
    }
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        return ResponseEntity.ok(new Object() {
            public Long getUnreadCount() { return 5L; }
        });
    }
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok("Marked as read");
    }
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok("Deleted");
    }
}