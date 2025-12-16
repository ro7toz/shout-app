package com.shout.controller;

import com.shout.model.Notification;
import com.shout.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {

        String username = extractUsername(principal);
        Page<Notification> notifications = notificationService.getUserNotifications(
                username, PageRequest.of(page, 20));
        
        Long unreadCount = notificationService.getUnreadCount(username);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentPage", page);
        
        return "notifications";
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public String markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        
        try {
            String username = extractUsername(principal);
            notificationService.markAsRead(id, username);
            return "success";
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return "error";
        }
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public String markAllAsRead(@AuthenticationPrincipal OAuth2User principal) {
        try {
            String username = extractUsername(principal);
            notificationService.markAllAsRead(username);
            return "success";
        } catch (Exception e) {
            log.error("Error marking all notifications as read", e);
            return "error";
        }
    }

    @GetMapping("/count")
    @ResponseBody
    public Long getUnreadCount(@AuthenticationPrincipal OAuth2User principal) {
        String username = extractUsername(principal);
        return notificationService.getUnreadCount(username);
    }

    @GetMapping("/dropdown")
    public String getNotificationDropdown(
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {
        
        String username = extractUsername(principal);
        Page<Notification> notifications = notificationService.getRecentNotifications(username);
        
        Long unreadCount = notificationService.getUnreadCount(username);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        
        return "fragments/notification :: dropdown";
    }

    private String extractUsername(OAuth2User principal) {
        String username = (String) principal.getAttributes().get("login");
        if (username == null) {
            username = principal.getAttribute("username");
        }
        if (username == null) {
            username = principal.getName();
        }
        return username;
    }
}
