package com.shout.api;

import com.shout.dto.*;
import com.shout.model.User;
import com.shout.service.UserService;
import com.shout.service.ShoutoutService;
import com.shout.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for serving page-level data to frontend
 * Combines data from multiple services for efficient page rendering
 */
@Slf4j
@RestController
@RequestMapping("/api/pages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoutoutService shoutoutService;

    /**
     * GET /api/pages/home
     * Returns home page data (logged in or not)
     */
    @GetMapping("/home")
    public ResponseEntity<?> getHomePageData() {
        try {
            Optional<User> currentUser = SecurityUtils.getCurrentUser();
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLoggedIn", currentUser.isPresent());
            
            if (currentUser.isPresent()) {
                UserProfileDTO userDTO = userService.getUserProfile(currentUser.get().getId());
                response.put("user", userDTO);
                response.put("page", "home-logged-in");
            } else {
                response.put("page", "home-logged-out");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching home page data", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/pages/dashboard
     * Returns dashboard page data (Pro analytics, exchanges)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardPageData() {
        try {
            Optional<User> currentUser = SecurityUtils.getCurrentUser();
            if (!currentUser.isPresent()) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
            
            Long userId = currentUser.get().getId();
            UserProfileDTO userDTO = userService.getUserProfile(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", userDTO);
            response.put("isPro", "PRO".equals(currentUser.get().getPlanType()));
            
            // Only fetch analytics if Pro user
            if ("PRO".equals(currentUser.get().getPlanType())) {
                // This would call analytics service
                // response.put("analytics", analyticsService.getUserAnalytics(userId));
            }
            
            // Get exchange history
            List<ExchangeDTO> exchanges = shoutoutService.getUserExchanges(userId);
            response.put("exchanges", exchanges);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching dashboard data", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/pages/profile/:userId
     * Returns user profile page data
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfilePageData(@PathVariable Long userId) {
        try {
            UserProfileDTO userDTO = userService.getUserProfile(userId);
            Optional<User> currentUser = SecurityUtils.getCurrentUser();
            
            if (currentUser.isPresent()) {
                userDTO.setIsOwnProfile(userDTO.getId().equals(currentUser.get().getId()));
            } else {
                userDTO.setIsOwnProfile(false);
            }
            
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.error("Error fetching profile page data", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/pages/notifications
     * Returns notifications page data
     */
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotificationsPageData() {
        try {
            Optional<User> currentUser = SecurityUtils.getCurrentUser();
            if (!currentUser.isPresent()) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
            
            // TODO: Implement notification fetching
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", List.of());
            response.put("unreadCount", 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching notifications", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/pages/payments
     * Returns payment page data
     */
    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentsPageData() {
        try {
            Optional<User> currentUser = SecurityUtils.getCurrentUser();
            if (!currentUser.isPresent()) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("currentPlan", currentUser.get().getPlanType());
            response.put("plans", List.of(
                Map.of(
                    "name", "Basic",
                    "price", "Free",
                    "features", List.of("10 requests/day", "Stories only", "No analytics")
                ),
                Map.of(
                    "name", "Pro Monthly",
                    "price", "₹999",
                    "currency", "INR",
                    "period", "month",
                    "features", List.of("50 requests/day", "All media types", "Advanced analytics")
                )
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching payments data", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/pages/static/:page
     * Returns static page data (Terms, Privacy, Refund)
     */
    @GetMapping("/static/{page}")
    public ResponseEntity<?> getStaticPage(@PathVariable String page) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            switch(page.toLowerCase()) {
                case "terms":
                    response.put("title", "Terms & Conditions");
                    response.put("content", getTermsContent());
                    break;
                case "privacy":
                    response.put("title", "Privacy Policy");
                    response.put("content", getPrivacyContent());
                    break;
                case "refund":
                    response.put("title", "Refund Policy");
                    response.put("content", getRefundContent());
                    break;
                default:
                    return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching static page", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getTermsContent() {
        return """        
        # Terms & Conditions
        
        ## 1. Acceptance of Terms
        By accessing and using ShoutX, you agree to be bound by these Terms and Conditions.
        
        ## 2. Service Description
        ShoutX is a platform that facilitates shoutout exchanges between Instagram users through Instagram Stories.
        
        ## 3. User Responsibilities
        Users agree to:
        - Use Instagram authentic accounts
        - Not engage in fraudulent activity
        - Comply with Instagram's terms of service
        - Respect other users' intellectual property
        
        ## 4. Exchange Rules
        - Basic users: 10 requests/day, Stories only
        - Pro users: 50 requests/day, All media types
        - 24-hour window for reposting
        - Failure to repost results in strikes
        - 3 strikes = account suspension
        
        ## 5. Prohibited Activities
        - Spam, harassment, or abuse
        - Commercial use without permission
        - Automated requests or bots
        - Manipulation of engagement metrics
        """;
    }

    private String getPrivacyContent() {
        return """        
        # Privacy Policy
        
        ## 1. Information We Collect
        - Instagram profile information (name, username, followers, profile picture)
        - Media shared on ShoutX
        - Engagement metrics and analytics
        - Payment information (processed by payment providers)
        - Communication records
        
        ## 2. How We Use Your Data
        - To facilitate shoutout exchanges
        - To provide analytics and reporting
        - To improve our services
        - To send notifications
        - To prevent fraud and abuse
        
        ## 3. Data Protection
        We implement industry-standard security measures to protect your data.
        
        ## 4. Third-Party Sharing
        We do not sell your personal data. We may share with:
        - Payment processors
        - Email service providers
        - Instagram (through API)
        
        ## 5. Your Rights
        You have the right to:
        - Access your data
        - Request deletion
        - Opt-out of communications
        """;
    }

    private String getRefundContent() {
        return """        
        # Refund Policy
        
        ## 1. Refund Eligibility
        Refunds are available within 7 days of purchase for Pro subscription plans.
        
        ## 2. Refund Request Process
        Contact support at tushkinit@gmail.com with:
        - Order ID
        - Reason for refund
        - Proof of payment
        
        ## 3. Refund Timeline
        Approved refunds are processed within 5-7 business days to the original payment method.
        
        ## 4. Non-Refundable Items
        - Exchange requests already completed
        - Partial month subscriptions
        - Services already provided
        
        ## 5. Disputes
        Unresolved disputes may be escalated to payment processor arbitration.
        """;
    }
}
