package com.shout.repository;

import com.shout.model.Payment;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByUser(User user);
    List<Payment> findByStatusAndCreatedAtBetween(Payment.PaymentStatus status, LocalDateTime start, LocalDateTime end);
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
}
