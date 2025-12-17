package com.shout.dto;

import com.shout.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private String subscriptionId; // Plan ID to upgrade to
    private Payment.PaymentGateway gateway; // RAZORPAY, PAYPAL, UPI, PAYTM
    private String billingCycle; // MONTHLY or YEARLY
    private BigDecimal amount; // Payment amount
}
