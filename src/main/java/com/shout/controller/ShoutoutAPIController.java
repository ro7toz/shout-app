package com.shout.controller;

import com.shout.dto.ExchangeDTO;
import com.shout.dto.ShoutoutRequestDTO;
import com.shout.model.*;
import com.shout.service.*;
import com.shout.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Shoutout API Controller - Handles shoutout requests and exchanges
 * Manages sending repost requests, accepting requests, and tracking exchanges
 */
@Slf4j
@RestController
@RequestMapping("/api/shoutouts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShoutoutAPIController {

    @Autowired
    private ShoutoutExchangeService shoutoutExchangeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * POST /api/shoutouts/send
     * Send a shoutout request to another user
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendShoutoutRequest(
            @RequestBody ShoutoutRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            // Verify sender authentication
            String token = jwtTokenProvider.getTokenFromRequest(httpRequest);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long senderId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> senderOptional = userService.findUserById(senderId);
            if (!senderOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User sender = senderOptional.get();

            // Validate recipient exists
            Optional<User> recipientOptional = userService.findUserById(request.getRecipientId());
            if (!recipientOptional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Recipient not found"));
            }

            User recipient = recipientOptional.get();

            // Check daily request limit
            int dailyLimit = sender.getPlanType().equals("PRO") ? 50 : 10;
            if (sender.getDailyRequestsSent() >= dailyLimit) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Daily request limit reached",
                    "limit", dailyLimit,
                    "used", sender.getDailyRequestsSent()
                ));
            }

            // Check if sender has 3+ strikes
            if (sender.getStrikes() >= 3) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Account deactivated due to violations",
                    "strikes", sender.getStrikes()
                ));
            }

            // Check if recipient is blocked or banned
            if (recipient.getStrikes() >= 3) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Cannot send request to deactivated account"
                ));
            }

            // Validate media type for Basic plan
            if (sender.getPlanType().equals("BASIC") && !request.getMediaType().equals("STORY")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Basic plan only allows STORY reposts. Upgrade to Pro for all media types."
                ));
            }

            // Create shoutout request
            ShoutoutRequest shoutoutRequest = new ShoutoutRequest();
            shoutoutRequest.setSender(sender);
            shoutoutRequest.setRecipient(recipient);
            shoutoutRequest.setMediaType(request.getMediaType());
            shoutoutRequest.setMediaUrl(request.getMediaUrl());
            shoutoutRequest.setMessage(request.getMessage());
            shoutoutRequest.setStatus("PENDING");
            shoutoutRequest.setCreatedAt(LocalDateTime.now());
            shoutoutRequest.setExpiresAt(LocalDateTime.now().plusHours(24));

            shoutoutRequest = shoutoutExchangeService.sendShoutoutRequest(shoutoutRequest);

            // Increment daily request counter
            sender.setDailyRequestsSent(sender.getDailyRequestsSent() + 1);
            userService.saveUser(sender);

            ShoutoutRequestDTO response = new ShoutoutRequestDTO();
            response.setId(shoutoutRequest.getId());
            response.setStatus("PENDING");
            response.setCreatedAt(shoutoutRequest.getCreatedAt());
            response.setExpiresAt(shoutoutRequest.getExpiresAt());

            log.info("Shoutout request sent from {} to {}", senderId, request.getRecipientId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error sending shoutout request", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/shoutouts/requests
     * Get pending shoutout requests for current user
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getPendingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            List<ShoutoutRequest> requests = shoutoutExchangeService.getPendingRequests(userId, page, pageSize);

            List<Map<String, Object>> response = new ArrayList<>();
            for (ShoutoutRequest sr : requests) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", sr.getId());
                dto.put("senderId", sr.getSender().getId());
                dto.put("senderName", sr.getSender().getUsername());
                dto.put("senderProfilePic", sr.getSender().getProfilePicture());
                dto.put("mediaType", sr.getMediaType());
                dto.put("mediaUrl", sr.getMediaUrl());
                dto.put("message", sr.getMessage());
                dto.put("createdAt", sr.getCreatedAt());
                dto.put("expiresAt", sr.getExpiresAt());
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching pending requests", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/shoutouts/requests/{requestId}/accept
     * Accept shoutout request and create exchange
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, Object> acceptRequest,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long recipientId = jwtTokenProvider.getUserIdFromToken(token);

            // Get the shoutout request
            Optional<ShoutoutRequest> shoutoutRequestOpt = shoutoutExchangeService.getShoutoutRequestById(requestId);
            if (!shoutoutRequestOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            ShoutoutRequest shoutoutRequest = shoutoutRequestOpt.get();

            // Verify recipient matches
            if (!shoutoutRequest.getRecipient().getId().equals(recipientId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            // Check if already expired
            if (LocalDateTime.now().isAfter(shoutoutRequest.getExpiresAt())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request expired"));
            }

            // Get selected media URL from request body
            String selectedMediaUrl = (String) acceptRequest.get("selectedMediaUrl");
            if (selectedMediaUrl == null || selectedMediaUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Selected media URL required"));
            }

            // Create exchange
            Exchange exchange = new Exchange();
            exchange.setShoutoutRequest(shoutoutRequest);
            exchange.setSender(shoutoutRequest.getSender());
            exchange.setRecipient(shoutoutRequest.getRecipient());
            exchange.setSenderMediaUrl(shoutoutRequest.getMediaUrl());
            exchange.setRecipientMediaUrl(selectedMediaUrl);
            exchange.setStatus("ACCEPTED");
            exchange.setCreatedAt(LocalDateTime.now());
            exchange.setDeadline(LocalDateTime.now().plusHours(24));
            exchange.setExchangedAt(null);

            exchange = shoutoutExchangeService.createExchange(exchange);

            // Update shoutout request status
            shoutoutRequest.setStatus("ACCEPTED");
            shoutoutExchangeService.updateShoutoutRequest(shoutoutRequest);

            // Increment recipient's daily requests accepted
            User recipient = shoutoutRequest.getRecipient();
            recipient.setDailyRequestsAccepted(recipient.getDailyRequestsAccepted() + 1);
            userService.saveUser(recipient);

            ExchangeDTO response = new ExchangeDTO();
            response.setId(exchange.getId());
            response.setStatus("ACCEPTED");
            response.setDeadline(exchange.getDeadline());
            response.setCreatedAt(exchange.getCreatedAt());

            log.info("Shoutout request accepted. Exchange created: {}", exchange.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error accepting request", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/shoutouts/exchanges
     * Get exchange history for current user
     */
    @GetMapping("/exchanges")
    public ResponseEntity<?> getExchanges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            List<Exchange> exchanges = shoutoutExchangeService.getUserExchanges(userId, page, pageSize);

            List<ExchangeDTO> response = new ArrayList<>();
            for (Exchange exchange : exchanges) {
                ExchangeDTO dto = new ExchangeDTO();
                dto.setId(exchange.getId());
                dto.setStatus(exchange.getStatus());
                dto.setCreatedAt(exchange.getCreatedAt());
                dto.setDeadline(exchange.getDeadline());
                dto.setExchangedAt(exchange.getExchangedAt());
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching exchanges", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/shoutouts/exchanges/{exchangeId}
     * Get exchange details
     */
    @GetMapping("/exchanges/{exchangeId}")
    public ResponseEntity<?> getExchangeDetails(@PathVariable Long exchangeId) {
        try {
            Optional<Exchange> exchangeOpt = shoutoutExchangeService.getExchangeById(exchangeId);
            if (!exchangeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Exchange exchange = exchangeOpt.get();
            ExchangeDTO dto = new ExchangeDTO();
            dto.setId(exchange.getId());
            dto.setStatus(exchange.getStatus());
            dto.setCreatedAt(exchange.getCreatedAt());
            dto.setDeadline(exchange.getDeadline());
            dto.setExchangedAt(exchange.getExchangedAt());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error fetching exchange details", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/shoutouts/exchanges/{exchangeId}/complete
     * Mark exchange as completed
     */
    @PostMapping("/exchanges/{exchangeId}/complete")
    public ResponseEntity<?> completeExchange(
            @PathVariable Long exchangeId,
            HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Exchange> exchangeOpt = shoutoutExchangeService.getExchangeById(exchangeId);

            if (!exchangeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Exchange exchange = exchangeOpt.get();

            // Verify user is part of exchange
            if (!exchange.getSender().getId().equals(userId) && !exchange.getRecipient().getId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            // Check if past deadline
            if (LocalDateTime.now().isAfter(exchange.getDeadline())) {
                // Add strike to non-compliant user
                User nonCompliant = exchange.getSender().getId().equals(userId) ? exchange.getRecipient() : exchange.getSender();
                nonCompliant.setStrikes(nonCompliant.getStrikes() + 1);
                userService.saveUser(nonCompliant);

                exchange.setStatus("INCOMPLETE");
                shoutoutExchangeService.updateExchange(exchange);

                return ResponseEntity.ok(Map.of(
                    "message", "Exchange marked incomplete - strike added",
                    "strikeCount", nonCompliant.getStrikes()
                ));
            }

            exchange.setStatus("COMPLETED");
            exchange.setExchangedAt(LocalDateTime.now());
            shoutoutExchangeService.updateExchange(exchange);

            return ResponseEntity.ok(Map.of("message", "Exchange completed successfully"));
        } catch (Exception e) {
            log.error("Error completing exchange", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
