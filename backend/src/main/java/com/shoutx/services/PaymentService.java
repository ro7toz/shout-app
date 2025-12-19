package com.shoutx.services;

import com.shoutx.models.Payment;
import com.shoutx.models.User;
import com.shoutx.repositories.PaymentRepository;
import com.shoutx.repositories.UserRepository;
import com.shoutx.dtos.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Payment createPayment(Long userId, PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setAmount(99); // Pro plan price
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setStatus("INITIATED");
        return paymentRepository.save(payment);
    }

    public String initiatePayment(Payment payment) {
        // Integrate with payment gateway (Razorpay, PayPal, etc.)
        return "https://payment-gateway.example.com/pay?id=" + payment.getPaymentId();
    }

    public Payment confirmPayment(String paymentId, Long userId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElse(null);
        
        if (payment != null && payment.getUserId().equals(userId)) {
            payment.setStatus("COMPLETED");
            paymentRepository.save(payment);
            
            // Upgrade user to PRO
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPlanType("PRO");
                userRepository.save(user);
            }
        }
        
        return payment;
    }

    public List<Payment> getUserPayments(Long userId, int page, int size) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, page, size);
    }
}
