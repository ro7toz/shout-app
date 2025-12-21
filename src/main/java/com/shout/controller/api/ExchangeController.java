package com.shout.controller;

import com.shout.dto.ExchangeDTO;
import com.shout.model.*;
import com.shout.repository.ShoutoutExchangeRepository;
import com.shout.service.*;
import com.shout.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Exchange Controller - Handles exchange lifecycle and verification
 * CRITICAL: Implements 24-hour timer, verification, and strike system
 */
@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExchangeController {
   
    private final ShoutoutExchangeRepository exchangeRepository;
    private final ShoutoutExchangeService exchangeService;
    private final ComplianceService complianceService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
   
    /**
     * GET /api/exchanges/{exchangeId}
     * Get exchange details with status and timer info
     */
    @GetMapping("/{exchangeId}")
    public ResponseEntity<?> getExchangeDetails(
            @PathVariable Long exchangeId,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
           
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
           
            // Authorization check
            if (!exchange.getRequester().getId().equals(userId) &&
                !exchange.getAcceptor().getId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }
           
            Map<String, Object> response = buildExchangeResponse(exchange, userId);
            return ResponseEntity.ok(response);
           
        } catch (Exception e) {
            log.error("Error fetching exchange details", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * POST /api/exchanges/{exchangeId}/confirm-repost
     * User confirms they posted their side of the exchange
     */
    @PostMapping("/{exchangeId}/confirm-repost")
    public ResponseEntity<?> confirmRepost(
            @PathVariable Long exchangeId,
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
           
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
           
            String postUrl = requestBody.get("postUrl");
           
            // Determine which side posted
            if (exchange.getRequester().getId().equals(userId)) {
                exchangeService.markRequesterPosted(exchange, postUrl);
            } else if (exchange.getAcceptor().getId().equals(userId)) {
                exchangeService.markAcceptorPosted(exchange, postUrl);
            } else {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }
           
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Verification scheduled");
            response.put("status", "PENDING_VERIFICATION");
            response.put("exchangeId", exchangeId);
           
            return ResponseEntity.ok(response);
           
        } catch (Exception e) {
            log.error("Error confirming repost", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * POST /api/exchanges/{exchangeId}/rate
     * Rate the other user after exchange completion
     */
    @PostMapping("/{exchangeId}/rate")
    public ResponseEntity<?> rateExchange(
            @PathVariable Long exchangeId,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
           
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
           
            // Check if exchange is complete
            if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.COMPLETED) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Exchange must be completed before rating")
                );
            }
           
            // Get rating details
            Integer rating = (Integer) requestBody.get("rating");
            Long ratedUserId = ((Number) requestBody.get("ratedUserId")).longValue();
           
            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Rating must be between 1 and 5")
                );
            }
           
            // TODO: Save rating to database
            // For now, just return success
           
            return ResponseEntity.ok(Map.of(
                "message", "Rating submitted successfully",
                "rating", rating
            ));
           
        } catch (Exception e) {
            log.error("Error rating exchange", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * GET /api/exchanges/user/active
     * Get all active exchanges for current user
     */
    @GetMapping("/user/active")
    public ResponseEntity<?> getUserActiveExchanges(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
           
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
           
            List<ShoutoutExchange> exchanges = exchangeService.getPendingExchanges(user);
           
            List<Map<String, Object>> response = new ArrayList<>();
            for (ShoutoutExchange exchange : exchanges) {
                response.add(buildExchangeResponse(exchange, userId));
            }
           
            return ResponseEntity.ok(response);
           
        } catch (Exception e) {
            log.error("Error fetching active exchanges", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   
    /**
     * Helper: Build exchange response with all status info
     */
    private Map<String, Object> buildExchangeResponse(ShoutoutExchange exchange, Long currentUserId) {
        Map<String, Object> response = new HashMap<>();
       
        response.put("id", exchange.getId());
        response.put("requesterUsername", exchange.getRequester().getUsername());
        response.put("requesterName", exchange.getRequester().getName());
        response.put("requesterProfilePic", exchange.getRequester().getProfilePicture());
        response.put("acceptorUsername", exchange.getAcceptor().getUsername());
        response.put("acceptorName", exchange.getAcceptor().getName());
        response.put("acceptorProfilePic", exchange.getAcceptor().getProfilePicture());
       
        // Status logic
        String status = determineStatus(exchange);
        response.put("status", status);
        response.put("timeStatus", determineTimeStatus(exchange));
       
        // Verification status
        response.put("requesterPosted", exchange.getRequesterPosted());
        response.put("acceptorPosted", exchange.getAcceptorPosted());
        response.put("requesterPostedAt", exchange.getRequesterPostedAt());
        response.put("acceptorPostedAt", exchange.getAcceptorPostedAt());
       
        // Timer information
        if (exchange.getExpiresAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration remaining = Duration.between(now, exchange.getExpiresAt());
           
            response.put("expiresAt", exchange.getExpiresAt());
            response.put("hoursRemaining", remaining.toHours());
            response.put("minutesRemaining", remaining.toMinutes() % 60);
        }
       
        // Media information
        response.put("postUrl", exchange.getPostUrl());
        response.put("requesterPostUrl", exchange.getRequesterPostUrl());
        response.put("acceptorPostUrl", exchange.getAcceptorPostUrl());
       
        // Pending from me?
        boolean isPendingFromMe = false;
        if (exchange.getRequester().getId().equals(currentUserId) && !exchange.getRequesterPosted()) {
            isPendingFromMe = true;
        } else if (exchange.getAcceptor().getId().equals(currentUserId) && !exchange.getAcceptorPosted()) {
            isPendingFromMe = true;
        }
        response.put("isPendingFromMe", isPendingFromMe);
       
        return response;
    }
   
    /**
     * Determine exchange status: Complete, Incomplete, Live
     */
    private String determineStatus(ShoutoutExchange exchange) {
        if (exchange.getRequesterPosted() && exchange.getAcceptorPosted()) {
            return "COMPLETE";
        }
        return "INCOMPLETE";
    }
   
    /**
     * Determine time status: Live or Expired
     */
    private String determineTimeStatus(ShoutoutExchange exchange) {
        if (exchange.getExpiresAt() == null) {
            return "LIVE";
        }
       
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exchange.getExpiresAt())) {
            return "EXPIRED";
        }
       
        return "LIVE";
    }
}
