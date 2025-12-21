package com.shout.controller.api;

import com.shout.dto.AuthResponse;
import com.shout.service.InstagramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class InstagramAuthController {

    private final InstagramAuthService instagramAuthService;

    @GetMapping("/instagram/callback")
    public ResponseEntity instagramCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        try {
            AuthResponse response = instagramAuthService.handleCallback(code, state);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Instagram callback failed", e);
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/instagram/exchange-token")
    public ResponseEntity exchangeToken(@RequestBody Map request) {
        try {
            String shortToken = request.get("accessToken");
            AuthResponse response = instagramAuthService.exchangeForLongLivedToken(shortToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
