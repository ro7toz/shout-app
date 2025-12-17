package com.shout.repository;

import com.shout.model.Subscription;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser(User user);
    List<Subscription> findByStatusAndRenewalDateBefore(Subscription.SubscriptionStatus status, LocalDateTime date);
    List<Subscription> findByAutoRenewAndRenewalDateBefore(Boolean autoRenew, LocalDateTime date);
}
