package com.shout.controller;

import com.shout.dto.NotificationDto;
import com.shout.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public String getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, 20);
        
        Page<NotificationDto> notifications = notificationService.getUserNotifications(username, pageable);
        long unreadCount = notificationService.getUnreadCount(username);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentPage", page);
        
        return "notifications";
    }

    @GetMapping("/api")
    @ResponseBody
    public Page<NotificationDto> getNotificationsApi(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        
        return notificationService.getUserNotifications(username, pageable);
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        Long unreadCount = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dropdown")
    @ResponseBody
    public Page<NotificationDto> getNotificationDropdown(Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(0, 5);
        return notificationService.getUserNotifications(username, pageable);
    }
}
