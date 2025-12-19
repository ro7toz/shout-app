package com.shoutx.service;

import com.shoutx.entity.Payment;
import com.shoutx.entity.User;
import com.shoutx.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    @Transactional
    public Payment initiatePayment(Long userId, User.PlanType planType, Payment.PaymentGateway gateway) {
        User user = userService.getUserById(userId);
        
        BigDecimal amount;
        if (planType.equals(User.PlanType.PRO)) {
            amount = new BigDecimal("999"); // Monthly pricing
        } else {
            throw new IllegalArgumentException("Invalid plan type");
        }
        
        Payment payment = Payment.builder()
                .user(user)
                .planType(planType)
                .amount(amount)
                .currency("INR")
                .paymentGateway(gateway)
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public void verifyAndCompletePayment(String transactionId, Payment.PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }
        
        Payment payment = paymentOpt.get();
        payment.setPaymentStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        
        if (status.equals(Payment.PaymentStatus.SUCCESS)) {
            // Upgrade user plan
            User user = payment.getUser();
            user.setPlanType(payment.getPlanType());
            user.setUpdatedAt(LocalDateTime.now());
            userService.updatePlan(user.getId(), payment.getPlanType());
            
            // Set validity
            payment.setValidFrom(LocalDate.now());
            payment.setValidTill(LocalDate.now().plusMonths(1));
            
            // Send confirmation email
            notificationService.sendPaymentSuccessNotification(user, payment);
            
            log.info("Payment {} successful. User {} upgraded to {}", transactionId, user.getId(), payment.getPlanType());
        }
        
        paymentRepository.save(payment);
    }
    
    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.getSuccessfulPaymentsForUser(userId);
    }
    
    public Boolean hasActiveProPlan(Long userId) {
        User user = userService.getUserById(userId);
        return user.getPlanType().equals(User.PlanType.PRO);
    }
    
    public Integer getDayRequestLimit(Long userId) {
        Boolean isPro = hasActiveProPlan(userId);
        return isPro ? 50 : 10;
    }
}
