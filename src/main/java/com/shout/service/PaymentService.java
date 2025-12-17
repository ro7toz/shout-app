package com.shout.service;

import com.shout.model.*;
import com.shout.repository.PaymentRepository;
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
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Create a new payment transaction
     */
    @Transactional
    public Payment initiatePayment(User user, Subscription subscription, BigDecimal amount,
                                   Payment.PaymentGateway gateway) {
        Payment payment = Payment.builder()
            .user(user)
            .subscription(subscription)
            .amount(amount)
            .gateway(gateway)
            .status(Payment.PaymentStatus.PENDING)
            .currency("INR")
            .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated for user {}: {} {}", user.getUsername(), amount, gateway);
        return saved;
    }

    /**
     * Confirm payment completion (called from gateway webhook)
     */
    @Transactional
    public Payment confirmPayment(String transactionId, String gatewayResponse) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());
        payment.setGatewayResponse(gatewayResponse);

        Payment saved = paymentRepository.save(payment);

        // Activate subscription
        if (payment.getSubscription() != null) {
            Subscription sub = payment.getSubscription();
            sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(sub);
        }

        log.info("Payment confirmed: {}", transactionId);
        return saved;
    }

    /**
     * Mark payment as failed
     */
    @Transactional
    public Payment failPayment(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment.setGatewayResponse(reason);

        Payment saved = paymentRepository.save(payment);
        log.warn("Payment failed: {} - {}", transactionId, reason);
        return saved;
    }

    /**
     * Process refund
     */
    @Transactional
    public Payment refundPayment(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Can only refund completed payments");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setGatewayResponse(reason);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment refunded: {} - {}", transactionId, reason);
        return saved;
    }

    /**
     * Get user's payment history
     */
    public List<Payment> getUserPayments(User user) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get payment by transaction ID
     */
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
}
