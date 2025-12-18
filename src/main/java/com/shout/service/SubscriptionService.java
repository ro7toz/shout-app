package com.shout.service;

import com.shout.model.*;
import com.shout.repository.SubscriptionPlanRepository;
import com.shout.repository.SubscriptionRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private static final int MAX_STRIKES = 3;

    /**
     * Initialize subscription plans (BASIC and PRO)
     * Call this once during app startup
     */
    @Transactional
    public void initializeSubscriptionPlans() {
        // ===== BASIC Plan =====
        Optional<SubscriptionPlan> basicPlan = subscriptionPlanRepository.findByPlanType(SubscriptionPlan.PlanType.BASIC);
        if (basicPlan.isEmpty()) {
            SubscriptionPlan basic = SubscriptionPlan.builder()
                .planType(SubscriptionPlan.PlanType.BASIC)
                .name("Basic")
                .description("Free plan - Stories only, no analytics")
                .monthlyPrice(BigDecimal.ZERO)
                .yearlyPrice(BigDecimal.ZERO)
                .originalYearlyPrice(BigDecimal.ZERO)
                .storiesSupported(true)
                .postsSupported(false)
                .reelsSupported(false)
                .analyticsSupported(false)
                .advancedAnalyticsSupported(false)
                .maxActiveRequests(10)
                .active(true)
                .build();
            subscriptionPlanRepository.save(basic);
            log.info("✅ BASIC subscription plan created");
        }

        // ===== PRO Plan =====
        Optional<SubscriptionPlan> proPlan = subscriptionPlanRepository.findByPlanType(SubscriptionPlan.PlanType.PRO);
        if (proPlan.isEmpty()) {
            SubscriptionPlan pro = SubscriptionPlan.builder()
                .planType(SubscriptionPlan.PlanType.PRO)
                .name("Pro")
                .description("Professional plan - Posts, Reels, Advanced Analytics")
                .monthlyPrice(new BigDecimal("499"))
                .yearlyPrice(new BigDecimal("4999"))
                .originalYearlyPrice(new BigDecimal("5988")) // 12 months * 499
                .storiesSupported(true)
                .postsSupported(true)
                .reelsSupported(true)
                .analyticsSupported(true)
                .advancedAnalyticsSupported(true)
                .maxActiveRequests(100)
                .active(true)
                .build();
            subscriptionPlanRepository.save(pro);
            log.info("✅ PRO subscription plan created");
        }
    }

    /**
     * Get a subscription plan by type
     */
    public SubscriptionPlan getPlan(SubscriptionPlan.PlanType planType) {
        return subscriptionPlanRepository.findByPlanType(planType)
            .orElseThrow(() -> new RuntimeException("Plan not found: " + planType));
    }

    /**
     * Create a new subscription for a user
     */
    @Transactional
    public Subscription createSubscription(User user, SubscriptionPlan plan, Subscription.BillingCycle cycle) {
        // Check if user already has active subscription
        Optional<Subscription> existing = subscriptionRepository.findByUser(user);
        if (existing.isPresent() && existing.get().getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
            log.warn("⚠️ User {} already has active subscription", user.getUsername());
            return existing.get();
        }

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime renewalDate = cycle == Subscription.BillingCycle.MONTHLY
            ? startDate.plusMonths(1)
            : startDate.plusYears(1);

        Subscription subscription = Subscription.builder()
            .user(user)
            .plan(plan)
            .billingCycle(cycle)
            .startDate(startDate)
            .renewalDate(renewalDate)
            .autoRenew(true)
            .status(Subscription.SubscriptionStatus.ACTIVE)
            .build();

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("✅ Subscription created for user: {} with plan: {} ({})",
            user.getUsername(), plan.getName(), cycle);

        return saved;
    }

    /**
     * Upgrade user subscription from BASIC to PRO
     * ✅ FIXED: Removes old subscription and creates new PRO subscription
     */
    @Transactional
    public Subscription upgradeSubscription(User user, Subscription.BillingCycle newCycle) {
        Optional<Subscription> existingOpt = subscriptionRepository.findByUser(user);

        // Cancel existing if active
        if (existingOpt.isPresent()) {
            Subscription existing = existingOpt.get();
            if (existing.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                existing.setStatus(Subscription.SubscriptionStatus.CANCELLED);
                existing.setCancelledDate(LocalDateTime.now());
                existing.setAutoRenew(false);
                subscriptionRepository.save(existing);
                log.info("🔄 Cancelled existing subscription for user: {}", user.getUsername());
            }
        }

        // Create new PRO subscription
        SubscriptionPlan proPlan = getPlan(SubscriptionPlan.PlanType.PRO);
        Subscription newSubscription = createSubscription(user, proPlan, newCycle);

        log.info("🎉 User {} upgraded to PRO ({})", user.getUsername(), newCycle);
        return newSubscription;
    }

    /**
     * Get user's current subscription
     */
    public Optional<Subscription> getUserSubscription(User user) {
        return subscriptionRepository.findByUser(user);
    }

    /**
     * ✅ CRITICAL METHOD: Check if user has PRO subscription
     * Queries Subscription table (NOT User enum)
     * This is the source of truth for PRO status
     */
    public boolean isProUser(User user) {
        Optional<Subscription> subscription = subscriptionRepository.findByUser(user);

        if (subscription.isEmpty()) {
            log.debug("❌ User {} has no subscription (BASIC by default)", user.getUsername());
            return false;
        }

        Subscription sub = subscription.get();
        boolean isPro = sub.getPlan().getPlanType() == SubscriptionPlan.PlanType.PRO
            && sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE;

        log.debug("✅ User {} is PRO: {}", user.getUsername(), isPro);
        return isPro;
    }

    /**
     * Check if user can access specific media type (STORY/POST/REEL)
     * STORY: All users
     * POST/REEL: PRO users only
     */
    public boolean canAccessMediaType(User user, ShoutoutRequest.MediaType mediaType) {
        if (mediaType == ShoutoutRequest.MediaType.STORY) {
            return true; // Stories available to all users (BASIC and PRO)
        }

        // Posts and Reels require PRO subscription
        boolean canAccess = isProUser(user);
        if (!canAccess) {
            log.warn("❌ User {} tried to access {} but not PRO", user.getUsername(), mediaType);
        }
        return canAccess;
    }

    /**
     * Cancel subscription
     */
    @Transactional
    public void cancelSubscription(User user) {
        Subscription subscription = subscriptionRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("No subscription found"));

        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setCancelledDate(LocalDateTime.now());
        subscription.setAutoRenew(false);
        subscriptionRepository.save(subscription);

        log.info("❌ Subscription cancelled for user: {}", user.getUsername());
    }

    /**
     * Process renewal for expired subscriptions
     * Runs periodically via scheduled task
     */
    @Transactional
    public void processExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubs = subscriptionRepository.findByAutoRenewAndRenewalDateBefore(true, now);

        for (Subscription sub : expiredSubs) {
            if (sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                sub.setStatus(Subscription.SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
                log.info("⏰ Subscription expired for user: {}", sub.getUser().getUsername());
            }
        }
    }
}
