package com.shoutx.repository;

import com.shoutx.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.paymentStatus = 'SUCCESS' " +
           "ORDER BY p.createdAt DESC")
    List<Payment> getSuccessfulPaymentsForUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'SUCCESS'")
    Long countSuccessfulPayments();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'SUCCESS'")
    Double getTotalRevenueFromPayments();
    
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :before")
    List<Payment> getExpiredPendingPayments(@Param("before") LocalDateTime before);
}
