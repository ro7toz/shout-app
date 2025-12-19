package com.shoutx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
    
    private Integer reach = 0;
    
    private Integer profileVisits = 0;
    
    private Integer clicks = 0;
    
    private Integer followersGained = 0;
    
    @Column(nullable = false)
    private LocalDate metricDate;
    
    @Enumerated(EnumType.STRING)
    private MetricType metricType;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum MetricType {
        DAILY, WEEKLY, MONTHLY, TOTAL
    }
}
