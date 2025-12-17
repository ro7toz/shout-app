package com.shout.service;

import com.shout.model.*;
import com.shout.repository.SubscriptionPlanRepository;
import com.shout.repository.SubscriptionRepository;
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

    /**
     * Initialize subscription plans (BASIC and PRO)
     */
    @Transactional
    public void initializeSubscriptionPlans() {
        // BASIC Plan
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
            log.info("BASIC subscription plan created");
        }

        // PRO Plan
        Optional<SubscriptionPlan> proPlan = subscriptionPlanRepository.findByPlanType(SubscriptionPlan.PlanType.PRO);
        if (proPlan.isEmpty()) {
            SubscriptionPlan pro = SubscriptionPlan.builder()
                .planType(SubscriptionPlan.PlanType.PRO)
                .name("Pro")
                .description("Professional plan - Posts, Reels, Advanced Analytics")
                .monthlyPrice(new BigDecimal("499"))
                .yearlyPrice(new BigDecimal("4999"))
                .originalYearlyPrice(new BigDecimal("24999"))
                .storiesSupported(true)
                .postsSupported(true)
                .reelsSupported(true)
                .analyticsSupported(true)
                .advancedAnalyticsSupported(true)
                .maxActiveRequests(50)
                .active(true)
                .build();
            subscriptionPlanRepository.save(pro);
            log.info("PRO subscription plan created");
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
        // Check if user already has subscription
        Optional<Subscription> existing = subscriptionRepository.findByUser(user);
        if (existing.isPresent() && existing.get().getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("User already has active subscription");
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
        log.info("Subscription created for user: {} with plan: {}", user.getUsername(), plan.getName());
        return saved;
    }

    /**
     * Upgrade user subscription from BASIC to PRO
     */
    @Transactional
    public Subscription upgradeSubscription(User user, Subscription.BillingCycle newCycle) {
        Subscription existing = subscriptionRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("No subscription found for user"));

        // Cancel existing if active
        if (existing.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
            existing.setStatus(Subscription.SubscriptionStatus.CANCELLED);
            existing.setCancelledDate(LocalDateTime.now());
        }

        // Create new PRO subscription
        SubscriptionPlan proPlan = getPlan(SubscriptionPlan.PlanType.PRO);
        return createSubscription(user, proPlan, newCycle);
    }

    /**
     * Get user's current subscription
     */
    public Optional<Subscription> getUserSubscription(User user) {
        return subscriptionRepository.findByUser(user);
    }

    /**
     * Check if user has PRO subscription
     */
    public boolean isProUser(User user) {
        Optional<Subscription> subscription = subscriptionRepository.findByUser(user);
        if (subscription.isEmpty()) {
            return false;
        }
        return subscription.get().getPlan().getPlanType() == SubscriptionPlan.PlanType.PRO
            && subscription.get().getStatus() == Subscription.SubscriptionStatus.ACTIVE;
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
        log.info("Subscription cancelled for user: {}", user.getUsername());
    }

    /**
     * Process renewal for expired subscriptions
     */
    @Transactional
    public void processExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubs = subscriptionRepository.findByAutoRenewAndRenewalDateBefore(true, now);
        
        for (Subscription sub : expiredSubs) {
            if (sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                sub.setStatus(Subscription.SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
                log.info("Subscription expired for user: {}", sub.getUser().getUsername());
            }
        }
    }
}
