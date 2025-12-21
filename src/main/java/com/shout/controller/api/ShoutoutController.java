package com.shout.controller;

import com.shout.dto.ShoutoutExchangeDTO;
import com.shout.model.*;
import com.shout.repository.ShoutoutRequestRepository;
import com.shout.service.NotificationService;
import com.shout.service.ShoutoutExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shoutouts")
@RequiredArgsConstructor
@Slf4j
public class ShoutoutController {
    private final ShoutoutExchangeService exchangeService;
    private final ShoutoutRequestRepository requestRepository;
    private final NotificationService notificationService;

    /**
     * Get pending exchanges for user
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingExchanges(@RequestAttribute("user") User user) {
        try {
            List<ShoutoutExchange> exchanges = exchangeService.getPendingExchanges(user);
            List<ShoutoutExchangeDTO> dtos = exchanges.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Error fetching pending exchanges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Mark as posted (requester)
     */
    @PostMapping("/{exchangeId}/mark-posted")
    public ResponseEntity<?> markAsPosted(@RequestAttribute("user") User user,
                                         @PathVariable Long exchangeId,
                                         @RequestParam String postUrl) {
        try {
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
            
            if (exchange.getRequester().getUsername().equals(user.getUsername())) {
                exchangeService.markRequesterPosted(exchange, postUrl);
            } else if (exchange.getAcceptor().getUsername().equals(user.getUsername())) {
                exchangeService.markAcceptorPosted(exchange, postUrl);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not part of this exchange");
            }

            return ResponseEntity.ok("Marked as posted");
        } catch (Exception e) {
            log.error("Error marking as posted", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get exchange details
     */
    @GetMapping("/{exchangeId}")
    public ResponseEntity<?> getExchangeDetails(@RequestAttribute("user") User user,
                                               @PathVariable Long exchangeId) {
        try {
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
            
            // Check authorization
            if (!exchange.getRequester().getUsername().equals(user.getUsername()) &&
                !exchange.getAcceptor().getUsername().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            return ResponseEntity.ok(toDTO(exchange));
        } catch (Exception e) {
            log.error("Error fetching exchange details", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Exchange not found"));
        }
    }

    // Helper method to convert to DTO
    private ShoutoutExchangeDTO toDTO(ShoutoutExchange exchange) {
        return ShoutoutExchangeDTO.builder()
            .id(exchange.getId())
            .requesterUsername(exchange.getRequester().getUsername())
            .acceptorUsername(exchange.getAcceptor().getUsername())
            .mediaType(exchange.getMediaType().name())
            .expiresAt(exchange.getExpiresAt())
            .status(exchange.getStatus().name())
            .requesterPosted(exchange.getRequesterPosted())
            .acceptorPosted(exchange.getAcceptorPosted())
            .requesterPostedAt(exchange.getRequesterPostedAt())
            .acceptorPostedAt(exchange.getAcceptorPostedAt())
            .postUrl(exchange.getPostUrl())
            .build();
    }

    private static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
