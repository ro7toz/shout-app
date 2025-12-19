package com.shoutx.services;

import com.shoutx.models.Analytics;
import com.shoutx.repositories.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public Analytics getUserAnalytics(Long userId) {
        return analyticsRepository.findByUserId(userId);
    }

    public double getAverageRating(Long userId) {
        return analyticsRepository.getAverageRating(userId);
    }

    public long getMonthlyFollowerGrowth(Long userId) {
        return analyticsRepository.getMonthlyFollowerGrowth(userId);
    }
}
