package com.shout.controller.api;

import com.shout.dto.AuthResponse;
import com.shout.service.InstagramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequiredArgsConstructor
@Slf4j
public class InstagramAuthController {

    private final InstagramAuthService instagramAuthService;

    /**
     * Instagram OAuth Callback - MAIN ENTRY POINT
     * Called by Instagram after user authorizes app
     */
    @GetMapping("/instagram/callback")
    public ResponseEntity<?> instagramCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        try {
            log.info("📥 Instagram callback received with code: {}", code.substring(0, 10) + "...");
            
            AuthResponse response = instagramAuthService.handleCallback(code, state);
            
            if (response.isSuccess()) {
                log.info("✅ Authentication successful for user: {}", response.getUser().getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.error("❌ Authentication failed: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("💥 Instagram callback error", e);
            return ResponseEntity.badRequest().body(
                AuthResponse.builder()
                    .success(false)
                    .message("Authentication failed: " + e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}
