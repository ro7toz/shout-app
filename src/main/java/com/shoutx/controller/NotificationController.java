package com.shoutx.controller;

import com.shoutx.model.Notification;
import com.shoutx.model.User;
import com.shoutx.service.UserService;
import com.shoutx.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * View all notifications for logged-in user
     */
    @GetMapping
    public String viewNotifications(Authentication authentication, Model model) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        List<Notification> notifications = notificationService.getUserNotifications(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("planType", user.getPlanType());
        model.addAttribute("showHeader", true);
        model.addAttribute("showFooter", true);
        
        setFooterData(model);
        return "notifications";
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/{notificationId}/read")
    public String markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            // TODO: Get notification from repository and mark as read
            // Notification notification = notificationRepository.findById(notificationId).orElseThrow();
            // notificationService.markAsRead(notification);
        } catch (Exception e) {
            return "redirect:/notifications?error=Notification not found";
        }

        return "redirect:/notifications";
    }

    /**
     * Get unread notification count (for header badge - HTMX)
     */
    @GetMapping("/count")
    @ResponseBody
    public long getUnreadCount(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return 0;

        return notificationService.getUnreadNotificationCount(user);
    }

    /**
     * Get recent unread notifications (for HTMX updates)
     */
    @GetMapping("/unread")
    public String getUnreadNotifications(Authentication authentication, Model model) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        List<Notification> unreadNotifications = notificationService.getUserNotifications(user)
                .stream()
                .filter(n -> !n.getIsRead())
                .toList();

        model.addAttribute("notifications", unreadNotifications);
        return "fragments/notification-items";
    }

    // Helper methods

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userService.getUserByUsername(authentication.getName()).orElse(null);
    }

    private void setFooterData(Model model) {
        model.addAttribute("companyName", "ShoutX");
        model.addAttribute("address", "Poonam Colony, Kota (Rajasthan)");
        model.addAttribute("phone", "+91 9509103148");
        model.addAttribute("email", "tushkinit@gmail.com");
    }
}
