package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(nullable = false, unique = true)
    private String transactionId; // Razorpay/PayPal order ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway; // RAZORPAY, PAYPAL, UPI, PAYTM

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(columnDefinition = "TEXT")
    private String gatewayResponse; // JSON response from payment gateway

    private String receipt; // For invoice purposes

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime processedAt; // When payment was confirmed

    public enum PaymentGateway {
        RAZORPAY,
        PAYPAL,
        UPI,
        PAYTM
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}
