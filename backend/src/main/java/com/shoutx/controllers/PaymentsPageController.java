package com.shoutx.controllers;

import com.shoutx.models.Plan;
import com.shoutx.models.Payment;
import com.shoutx.services.PaymentService;
import com.shoutx.services.PlanService;
import com.shoutx.dtos.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pages/payments")
public class PaymentsPageController {

    @Autowired
    private PlanService planService;
    
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        List<Plan> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/upgrade-to-pro")
    public ResponseEntity<?> upgradeToPromos(
        @RequestBody PaymentDTO paymentDTO,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        
        try {
            Payment payment = paymentService.createPayment(userId, paymentDTO);
            // Redirect to payment gateway (Razorpay, PayPal, etc.)
            return ResponseEntity.ok(new Object() {
                public String getPaymentUrl() { return paymentService.initiatePayment(payment); }
                public String getPaymentId() { return payment.getPaymentId(); }
            });
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error initiating payment: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(
        @RequestParam String paymentId,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        
        try {
            Payment payment = paymentService.confirmPayment(paymentId, userId);
            return ResponseEntity.ok(new Object() {
                public String getStatus() { return payment.getStatus(); }
                public String getMessage() { return "Upgrade successful!"; }
            });
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error confirming payment: " + e.getMessage());
        }
    }

    @GetMapping("/payment-history")
    public ResponseEntity<?> getPaymentHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        List<Payment> payments = paymentService.getUserPayments(userId, page, size);
        return ResponseEntity.ok(payments);
    }
}
