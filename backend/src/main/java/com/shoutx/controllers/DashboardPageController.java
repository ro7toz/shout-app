package com.shoutx.controllers;

import com.shoutx.models.Analytics;
import com.shoutx.models.Exchange;
import com.shoutx.services.AnalyticsService;
import com.shoutx.services.ExchangeService;
import com.shoutx.dtos.AnalyticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pages/dashboard")
public class DashboardPageController {

    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private ExchangeService exchangeService;

    @GetMapping
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        
        Analytics analytics = analyticsService.getUserAnalytics(userId);
        AnalyticsDTO dto = new AnalyticsDTO(
            analytics.getTotalReach(),
            analytics.getProfileVisits(),
            analytics.getRepostsCount(),
            analytics.getCompletionRate(),
            analytics.getFollowerGrowth()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/exchanges")
    public ResponseEntity<?> getRecentExchanges(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        List<Exchange> exchanges = exchangeService.getUserExchanges(userId, page, size);
        return ResponseEntity.ok(exchanges);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        
        return ResponseEntity.ok(new Object() {
            public long getTotalExchanges() { return exchangeService.countUserExchanges(userId); }
            public double getAverageRating() { return analyticsService.getAverageRating(userId); }
            public long getFollowerGrowthThisMonth() { return analyticsService.getMonthlyFollowerGrowth(userId); }
        });
    }
}
