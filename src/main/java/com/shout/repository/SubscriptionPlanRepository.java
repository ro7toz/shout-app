package com.shout.repository;

import com.shout.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByPlanType(SubscriptionPlan.PlanType planType);
    Optional<SubscriptionPlan> findByName(String name);
}
