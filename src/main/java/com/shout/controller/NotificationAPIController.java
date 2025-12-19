package com.shout.controller;

import com.shout.model.Notification;
import com.shout.service.NotificationService;
import com.shout.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Notification API Controller - Handles notifications for users
 * Manages fetching, reading, and deleting notifications
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationAPIController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/notifications
     * Get all notifications for current user
     */
    @GetMapping()
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            List<Notification> notifications;

            if (type != null) {
                notifications = notificationService.getNotificationsByType(userId, type, page, pageSize);
            } else {
                notifications = notificationService.getUserNotifications(userId, page, pageSize);
            }

            List<Map<String, Object>> response = new ArrayList<>();
            for (Notification n : notifications) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", n.getId());
                dto.put("type", n.getType()); // REQUEST_RECEIVED, REQUEST_ACCEPTED, EXCHANGE_COMPLETED, STRIKE_ADDED, PLAN_UPGRADED
                dto.put("title", n.getTitle());
                dto.put("message", n.getMessage());
                dto.put("isRead", n.getIsRead());
                dto.put("createdAt", n.getCreatedAt());
                dto.put("relatedUser", n.getRelatedUserId());
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching notifications", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/notifications/unread/count
     * Get count of unread notifications
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            long unreadCount = notificationService.getUnreadCount(userId);

            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            log.error("Error fetching unread count", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/notifications/{id}/read
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Notification> notificationOpt = notificationService.getNotification(id);

            if (!notificationOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();

            // Verify user owns this notification
            if (!notification.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            notification.setIsRead(true);
            notificationService.saveNotification(notification);

            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/notifications/{id}
     * Delete notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Notification> notificationOpt = notificationService.getNotification(id);

            if (!notificationOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();

            // Verify user owns this notification
            if (!notification.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            notificationService.deleteNotification(id);

            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting notification", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/notifications/mark-all-read
     * Mark all notifications as read
     */
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            notificationService.markAllAsRead(userId);

            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            log.error("Error marking all as read", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
