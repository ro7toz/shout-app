package com.shoutx.controller;

import com.shoutx.model.User;
import com.shoutx.service.ShoutoutExchangeService;
import com.shoutx.service.UserService;
import com.shoutx.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ShoutoutExchangeService exchangeService;
    private final NotificationService notificationService;

    @GetMapping
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return homeLoggedIn(authentication, model);
        }
        return homeNotLoggedIn(model);
    }

    /**
     * Homepage without login
     * Header: Logo, Company Name | Plans & Pricing | Login | Get Started
     * Body: Hero Section, Features, FAQs
     * Footer: About Links, Contact Info, Social
     */
    @GetMapping("home")
    public String homeNotLoggedIn(Model model) {
        // Set header properties
        model.addAttribute("isLoggedIn", false);
        model.addAttribute("showHeader", true);
        model.addAttribute("showFooter", true);
        
        // FAQ data
        model.addAttribute("faqs", getFAQs());
        
        // Company info for footer
        model.addAttribute("companyName", "ShoutX");
        model.addAttribute("address", "Poonam Colony, Kota (Rajasthan)");
        model.addAttribute("phone", "+91 9509103148");
        model.addAttribute("email", "tushkinit@gmail.com");
        
        return "home";
    }

    /**
     * Homepage after login
     * Displays dashboard with Send ShoutOuts and Requests tabs
     * Default tab: Send ShoutOuts
     */
    @GetMapping("dashboard")
    public String homeLoggedIn(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        // Check if account is banned
        if (user.getIsAccountBanned() || user.getIsAccountDeleted()) {
            return "redirect:/account-banned";
        }

        // Set header properties
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("user", user);
        model.addAttribute("planType", user.getPlanType());
        model.addAttribute("showHeader", true);
        model.addAttribute("showFooter", true);

        // Notifications
        long unreadCount = notificationService.getUnreadNotificationCount(user);
        model.addAttribute("unreadNotificationCount", unreadCount);

        // Get users for "Send ShoutOuts" tab (sorted by follower count)
        List<User> availableUsers = userService.getActiveUsers()
                .stream()
                .filter(u -> !u.getId().equals(user.getId())) // Exclude self
                .limit(50)
                .toList();
        model.addAttribute("availableUsers", availableUsers);

        // Get pending requests for "Requests" tab
        model.addAttribute("pendingRequests", exchangeService.getPendingRequests(user));

        // Default tab is "Send ShoutOuts"
        model.addAttribute("activeTab", "sendShoutouts");

        // Company info for footer
        setFooterData(model);

        return "dashboard";
    }

    /**
     * Render just the Send ShoutOuts tab (HTMX endpoint)
     */
    @GetMapping("api/dashboard/send-shoutouts")
    public String sendShoutoutsTab(Authentication authentication, Model model) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        List<User> availableUsers = userService.getActiveUsers()
                .stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .limit(50)
                .toList();
        
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("user", user);
        return "fragments/send-shoutouts-tab";
    }

    /**
     * Render just the Requests tab (HTMX endpoint)
     */
    @GetMapping("api/dashboard/requests")
    public String requestsTab(Authentication authentication, Model model) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        model.addAttribute("pendingRequests", exchangeService.getPendingRequests(user));
        model.addAttribute("user", user);
        return "fragments/requests-tab";
    }

    /**
     * Filter users by follower count range (HTMX endpoint)
     */
    @GetMapping("api/users/filter")
    public String filterUsers(
            String followerRange,
            String genre,
            String repostType,
            Authentication authentication,
            Model model) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        List<User> filteredUsers = getFilteredUsers(followerRange, user);
        model.addAttribute("users", filteredUsers);
        return "fragments/user-cards";
    }

    // Helper methods

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userService.getUserByUsername(authentication.getName()).orElse(null);
    }

    private List<User> getFilteredUsers(String followerRange, User currentUser) {
        // Parse follower range and fetch users
        // Format: "0-99", "100-499", "500-999", etc.
        if (followerRange == null || followerRange.isEmpty()) {
            return userService.getActiveUsers().stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .limit(50)
                    .toList();
        }

        long minFollowers = parseFollowerRange(followerRange)[0];
        long maxFollowers = parseFollowerRange(followerRange)[1];
        
        return userService.searchUsersByFollowerRange(minFollowers, maxFollowers).stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .limit(50)
                .toList();
    }

    private long[] parseFollowerRange(String range) {
        switch (range) {
            case "0-99" -> { return new long[]{0, 99}; }
            case "100-499" -> { return new long[]{100, 499}; }
            case "500-999" -> { return new long[]{500, 999}; }
            case "1k-2.5k" -> { return new long[]{1000, 2500}; }
            case "2.5k-5k" -> { return new long[]{2500, 5000}; }
            case "5k-10k" -> { return new long[]{5000, 10000}; }
            case "10k-25k" -> { return new long[]{10000, 25000}; }
            case "25k-50k" -> { return new long[]{25000, 50000}; }
            case "50k-100k" -> { return new long[]{50000, 100000}; }
            case "100k-250k" -> { return new long[]{100000, 250000}; }
            case "250k-500k" -> { return new long[]{250000, 500000}; }
            case "500k-1m" -> { return new long[]{500000, 1000000}; }
            case "1m-2.5m" -> { return new long[]{1000000, 2500000}; }
            case "2.5m-5m+" -> { return new long[]{2500000, Long.MAX_VALUE}; }
            default -> { return new long[]{0, Long.MAX_VALUE}; }
        }
    }

    private List<String> getFAQs() {
        return List.of(
            "How does ShoutX work? - ShoutX is a platform where influencers exchange shoutouts. Send a request to another user, accept theirs, and repost to grow together!",
            "What are the plan limits? - Basic (Free): 10 requests/day, Stories only. Pro: 50 requests/day, All media types.",
            "What happens if I don't repost? - Non-compliance results in strikes. 3 strikes = account ban and permanent Instagram login ban.",
            "How long do I have to repost? - You have 24 hours to repost after accepting an exchange.",
            "Can I delete my media? - Yes, but you must have at least 1 photo to keep your account active.",
            "How is my account verified? - We check your Instagram verification status automatically."
        );
    }

    private void setFooterData(Model model) {
        model.addAttribute("companyName", "ShoutX");
        model.addAttribute("address", "Poonam Colony, Kota (Rajasthan)");
        model.addAttribute("phone", "+91 9509103148");
        model.addAttribute("email", "tushkinit@gmail.com");
    }
}
