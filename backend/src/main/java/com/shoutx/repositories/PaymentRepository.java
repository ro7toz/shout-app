package com.shoutx.repositories;

import com.shoutx.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = ?1 ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size);
    
    Page<Payment> findByUserId(Long userId, Pageable pageable);
}
