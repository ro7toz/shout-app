package com.shoutx.repository;

import com.shoutx.model.Payment;
import com.shoutx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByUser(User user);

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status = 'COMPLETED' ORDER BY p.processedAt DESC")
    List<Payment> findSuccessfulPayments(User user);

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status = 'COMPLETED' AND p.planType = 'PRO' ORDER BY p.expiresAt DESC LIMIT 1")
    Optional<Payment> findLatestActiveProSubscription(User user);
}
