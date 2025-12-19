package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
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
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private User.PlanType planType;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    private String currency = "INR";
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;
    
    @Column(unique = true)
    private String transactionId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    private String orderId;
    
    private String razorpayPaymentId;
    
    private String razorpayOrderId;
    
    private String razorpaySignature;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    private LocalDate validFrom;
    
    private LocalDate validTill;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum PaymentGateway {
        RAZORPAY, PAYPAL, PAYTM, UPI
    }
    
    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, CANCELLED
    }
    
    public enum PaymentMethod {
        UPI, CARD, NETBANKING, WALLET, PAYPAL
    }
}
