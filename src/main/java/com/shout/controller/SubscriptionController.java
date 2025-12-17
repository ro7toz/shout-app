package com.shout.controller;

import com.shout.dto.SubscriptionDTO;
import com.shout.model.*;
import com.shout.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    /**
     * Get current user's subscription
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription(@RequestAttribute("user") User user) {
        try {
            Optional<Subscription> subscription = subscriptionService.getUserSubscription(user);
            if (subscription.isEmpty()) {
                return ResponseEntity.ok("No active subscription");
            }
            return ResponseEntity.ok(subscription.get());
        } catch (Exception e) {
            log.error("Error fetching subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Check if user is PRO
     */
    @GetMapping("/is-pro")
    public ResponseEntity<?> isProUser(@RequestAttribute("user") User user) {
        boolean isPro = subscriptionService.isProUser(user);
        return ResponseEntity.ok(new IsPro Response(isPro));
    }

    /**
     * Upgrade to PRO (monthly)
     */
    @PostMapping("/upgrade/monthly")
    public ResponseEntity<?> upgradeToProMonthly(@RequestAttribute("user") User user) {
        try {
            Subscription subscription = subscriptionService.upgradeSubscription(
                user, Subscription.BillingCycle.MONTHLY
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (Exception e) {
            log.error("Error upgrading subscription", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Upgrade to PRO (yearly)
     */
    @PostMapping("/upgrade/yearly")
    public ResponseEntity<?> upgradeToProYearly(@RequestAttribute("user") User user) {
        try {
            Subscription subscription = subscriptionService.upgradeSubscription(
                user, Subscription.BillingCycle.YEARLY
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (Exception e) {
            log.error("Error upgrading subscription", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel subscription
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelSubscription(@RequestAttribute("user") User user) {
        try {
            subscriptionService.cancelSubscription(user);
            return ResponseEntity.ok("Subscription cancelled");
        } catch (Exception e) {
            log.error("Error cancelling subscription", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Inner class for response
    private static class IsProResponse {
        public boolean isPro;
        public IsProResponse(boolean isPro) {
            this.isPro = isPro;
        }
    }
}
