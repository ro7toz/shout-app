package com.shout.controller;

import com.shout.dto.AnalyticsDTO;
import com.shout.model.*;
import com.shout.repository.PostAnalyticsRepository;
import com.shout.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    private final PostAnalyticsRepository analyticsRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Get user's analytics dashboard (PRO only)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestAttribute("user") User user) {
        try {
            // Check if PRO user
            if (!subscriptionService.isProUser(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Upgrade to PRO to access analytics", "PRO_REQUIRED"));
            }

            // Get all analytics for user
            List<PostAnalytics> analytics = analyticsRepository.findByUser(user);

            // Calculate aggregates
            Long totalImpressions = analyticsRepository.getTotalImpressions(user);
            Long totalClicks = analyticsRepository.getTotalClicks(user);
            Long totalProfileVisits = analyticsRepository.getTotalProfileVisits(user);
            Double avgEngagement = analyticsRepository.getAverageEngagementRate(user);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalImpressions", totalImpressions != null ? totalImpressions : 0);
            dashboard.put("totalClicks", totalClicks != null ? totalClicks : 0);
            dashboard.put("totalProfileVisits", totalProfileVisits != null ? totalProfileVisits : 0);
            dashboard.put("averageEngagementRate", avgEngagement != null ? avgEngagement : 0.0);
            dashboard.put("posts", analytics.size());

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "ERROR"));
        }
    }

    /**
     * Get filtered analytics by month
     */
    @GetMapping("/dashboard/monthly/{yearMonth}")
    public ResponseEntity<?> getMonthlyAnalytics(@RequestAttribute("user") User user,
                                                @PathVariable String yearMonth) {
        try {
            if (!subscriptionService.isProUser(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Upgrade to PRO to access analytics", "PRO_REQUIRED"));
            }

            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDateTime startDate = ym.atDay(1).atStartOfDay();
            LocalDateTime endDate = ym.atEndOfMonth().atTime(23, 59, 59);

            List<PostAnalytics> analytics = analyticsRepository.findByUserAndCreatedAtBetween(user, startDate, endDate);

            Map<String, Object> data = new HashMap<>();
            data.put("month", yearMonth);
            data.put("posts", analytics.size());
            data.put("totalImpressions", analytics.stream().mapToLong(a -> a.getImpressions() != null ? a.getImpressions() : 0).sum());
            data.put("totalClicks", analytics.stream().mapToLong(a -> a.getClicks() != null ? a.getClicks() : 0).sum());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error fetching monthly analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "ERROR"));
        }
    }

    /**
     * Get analytics by media type
     */
    @GetMapping("/dashboard/by-type/{mediaType}")
    public ResponseEntity<?> getAnalyticsByType(@RequestAttribute("user") User user,
                                               @PathVariable String mediaType) {
        try {
            if (!subscriptionService.isProUser(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Upgrade to PRO to access analytics", "PRO_REQUIRED"));
            }

            List<PostAnalytics> analytics = analyticsRepository.findByUser(user).stream()
                .filter(a -> a.getMediaType().name().equals(mediaType.toUpperCase()))
                .collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("mediaType", mediaType);
            data.put("count", analytics.size());
            data.put("analytics", analytics);

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error fetching analytics by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "ERROR"));
        }
    }

    // Error response class
    private static class ErrorResponse {
        public String message;
        public String code;

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }
    }
}
