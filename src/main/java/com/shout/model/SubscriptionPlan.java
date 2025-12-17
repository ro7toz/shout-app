package com.shout.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanType planType; // BASIC, PRO

    @Column(nullable = false)
    private String name; // "Basic", "Pro"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal monthlyPrice; // 0 for BASIC, 499 for PRO

    @Column(nullable = false)
    private BigDecimal yearlyPrice; // 0 for BASIC, 4999 for PRO

    @Column(nullable = false)
    private BigDecimal originalYearlyPrice; // 24999 for PRO (before slashing)

    @Column(nullable = false)
    private Boolean storiesSupported = true; // Both support stories

    @Column(nullable = false)
    private Boolean postsSupported = false; // Only PRO

    @Column(nullable = false)
    private Boolean reelsSupported = false; // Only PRO

    @Column(nullable = false)
    private Boolean analyticsSupported = false; // Only PRO

    @Column(nullable = false)
    private Boolean advancedAnalyticsSupported = false; // Only PRO

    @Column(nullable = false)
    private Integer maxActiveRequests = 10; // Limit concurrent requests

    @Column(nullable = false)
    private Boolean active = true;

    public enum PlanType {
        BASIC,
        PRO
    }
}
