package com.shout.dto;

import com.shout.model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String planName;
    private String planType;
    private String billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime renewalDate;
    private String status;
    private Boolean autoRenew;
}
