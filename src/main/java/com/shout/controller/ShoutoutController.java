package com.shout.controller;

import com.shout.dto.CreateRequestDto;
import com.shout.dto.ShoutoutRequestDto;
import com.shout.service.ShoutoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoutout")
@RequiredArgsConstructor
@Slf4j
public class ShoutoutController {
    private final ShoutoutService shoutoutService;

    @PostMapping("/request")
    public ResponseEntity<?> createRequest(
            Authentication authentication,
            @Valid @RequestBody CreateRequestDto request) {
        try {
            String requesterUsername = authentication.getName();
            
            var shoutoutRequest = shoutoutService.createRequest(
                requesterUsername,
                request.getTargetUsername(),
                request.getPostLink()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(shoutoutRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(
            Authentication authentication,
            @PathVariable Long requestId) {
        try {
            String targetUsername = authentication.getName();
            
            var shoutoutRequest = shoutoutService.acceptRequest(requestId, targetUsername);
            
            return ResponseEntity.ok(shoutoutRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{requestId}/posted")
    public ResponseEntity<?> markAsPosted(
            Authentication authentication,
            @PathVariable Long requestId) {
        try {
            String username = authentication.getName();
            
            var shoutoutRequest = shoutoutService.markAsPosted(requestId, username);
            
            return ResponseEntity.ok(shoutoutRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ShoutoutRequestDto>> getPendingRequests(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        
        var requests = shoutoutService.getPendingRequests(username, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<ShoutoutRequestDto>> getSentRequests(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        
        var requests = shoutoutService.getSentRequests(username, pageable);
        return ResponseEntity.ok(requests);
    }
}
