package com.shout.controller;

import com.shout.dto.PaymentRequestDTO;
import com.shout.model.*;
import com.shout.service.PaymentService;
import com.shout.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    /**
     * Initiate payment
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestAttribute("user") User user,
                                            @RequestBody PaymentRequestDTO request) {
        try {
            SubscriptionPlan plan = subscriptionService.getPlan(SubscriptionPlan.PlanType.PRO);
            
            // Create subscription (pending payment)
            Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .billingCycle("MONTHLY".equals(request.getBillingCycle()) 
                    ? Subscription.BillingCycle.MONTHLY 
                    : Subscription.BillingCycle.YEARLY)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .autoRenew(true)
                .build();

            // Create payment
            Payment payment = paymentService.initiatePayment(
                user, subscription, request.getAmount(), request.getGateway()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            log.error("Error initiating payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Webhook: Confirm payment (called by payment gateway)
     */
    @PostMapping("/webhook/confirm")
    public ResponseEntity<?> confirmPayment(@RequestParam String transactionId,
                                           @RequestParam String status,
                                           @RequestBody(required = false) String gatewayResponse) {
        try {
            if ("success".equalsIgnoreCase(status)) {
                paymentService.confirmPayment(transactionId, gatewayResponse);
                return ResponseEntity.ok("Payment confirmed");
            } else {
                paymentService.failPayment(transactionId, gatewayResponse);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed");
            }
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Get payment status
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String transactionId) {
        try {
            var payment = paymentService.getPaymentByTransactionId(transactionId);
            if (payment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
            }
            return ResponseEntity.ok(payment.get());
        } catch (Exception e) {
            log.error("Error getting payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
