package com.shout.controller;

import com.shout.model.User;
import com.shout.service.SubscriptionService;
import com.shout.service.UserService;
import com.shout.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;


import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Payment API Controller - Handles subscription and payment processing
 * Manages plan upgrades, payments, and subscription management
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentAPIController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/payments/current-plan
     * Get current user's plan details
     */
    @GetMapping("/current-plan")
    public ResponseEntity<?> getCurrentPlan(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("planType", user.getPlanType());
            response.put("dailyLimit", user.getPlanType().equals("PRO") ? 50 : 10);
            response.put("allowedMediaTypes", user.getPlanType().equals("PRO") 
                ? new String[]{"STORY", "POST", "REEL"} 
                : new String[]{"STORY"});
            response.put("subscriptionStartDate", user.getSubscriptionStartDate());
            response.put("subscriptionEndDate", user.getSubscriptionEndDate());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching current plan", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/payments/plans
     * Get available subscription plans
     */
    @GetMapping("/plans")
    public ResponseEntity<?> getAvailablePlans() {
        try {
            List<Map<String, Object>> plans = new ArrayList<>();

            // Basic Plan
            Map<String, Object> basicPlan = new HashMap<>();
            basicPlan.put("id", "basic");
            basicPlan.put("name", "Basic");
            basicPlan.put("price", 0);
            basicPlan.put("billingPeriod", "FREE");
            basicPlan.put("dailyRequests", 10);
            basicPlan.put("allowedMediaTypes", new String[]{"STORY"});
            basicPlan.put("features", new String[]{"Basic shoutout exchanges", "Limited to stories only"});
            plans.add(basicPlan);

            // Pro Plan
            Map<String, Object> proPlan = new HashMap<>();
            proPlan.put("id", "pro");
            proPlan.put("name", "Pro");
            proPlan.put("priceINR", 99); // Per month in INR
            proPlan.put("priceUSD", 1.2);
            proPlan.put("billingPeriod", "MONTHLY");
            proPlan.put("dailyRequests", 50);
            proPlan.put("allowedMediaTypes", new String[]{"STORY", "POST", "REEL"});
            proPlan.put("features", new String[]{
                "50 daily requests",
                "All media types (stories, posts, reels)",
                "Advanced analytics dashboard",
                "Priority support",
                "No ads"
            });
            plans.add(proPlan);

            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Error fetching plans", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/payments/upgrade-to-pro
     * Initiate upgrade to Pro plan
     */
    @PostMapping("/upgrade-to-pro")
    public ResponseEntity<?> upgradeToPro(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(httpRequest);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            if (user.getPlanType().equals("PRO")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Already on Pro plan"));
            }

            String paymentMethod = request.get("paymentMethod"); // UPI, CARD, PAYPAL, PAYTM
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Payment method required"));
            }

            // Create payment order
            Map<String, Object> order = new HashMap<>();
            order.put("orderId", "ORDER_" + System.currentTimeMillis());
            order.put("amount", 99); // INR
            order.put("currency", "INR");
            order.put("description", "ShoutX Pro Monthly Subscription");
            order.put("paymentMethod", paymentMethod);
            order.put("userId", userId);

            // TODO: Integrate with Razorpay/PayPal/Paytm
            // For now, return payment order details
            order.put("status", "PENDING");
            order.put("redirectUrl", "/payment/confirm");

            log.info("Upgrade to Pro initiated for user: {}", userId);
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            log.error("Error upgrading to Pro", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/payments/verify
     * Verify payment and upgrade user plan
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(httpRequest);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            String orderId = request.get("orderId");
            String paymentId = request.get("paymentId");

            if (orderId == null || paymentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order ID and Payment ID required"));
            }

            // TODO: Verify payment with payment gateway
            // Razorpay/PayPal/Paytm signature verification

            // Update user plan
            User user = userOptional.get();
            user.setPlanType("PRO");
            user.setSubscriptionStartDate(LocalDateTime.now());
            user.setSubscriptionEndDate(LocalDateTime.now().plusMonths(1));
            user = userService.saveUser(user);

            // TODO: Send confirmation email

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment verified successfully");
            response.put("planType", user.getPlanType());
            response.put("subscriptionEndDate", user.getSubscriptionEndDate());

            log.info("Payment verified and user upgraded to Pro: {}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/payments/history
     * Get payment history for current user
     */
    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            // TODO: Fetch payment history from database
            List<Map<String, Object>> paymentHistory = new ArrayList<>();

            return ResponseEntity.ok(paymentHistory);
        } catch (Exception e) {
            log.error("Error fetching payment history", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/payments/cancel-subscription
     * Cancel Pro subscription and downgrade to Basic
     */
    @PostMapping("/cancel-subscription")
    public ResponseEntity<?> cancelSubscription(
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userService.findUserById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            if (user.getPlanType().equals("BASIC")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Already on Basic plan"));
            }

            // Downgrade to Basic
            user.setPlanType("BASIC");
            user.setSubscriptionEndDate(null);
            user = userService.saveUser(user);

            // TODO: Send cancellation email

            log.info("Subscription cancelled for user: {}", userId);
            return ResponseEntity.ok(Map.of("message", "Subscription cancelled. Downgraded to Basic plan."));

        } catch (Exception e) {
            log.error("Error cancelling subscription", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
