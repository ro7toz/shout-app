package com.shoutx.controller;

import com.shoutx.dto.LoginRequest;
import com.shoutx.dto.SignupRequest;
import com.shoutx.dto.AuthResponse;
import com.shoutx.entity.User;
import com.shoutx.service.UserService;
import com.shoutx.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        User user = userService.registerUser(request.getEmail(), request.getName(), request.getPassword());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .token(token)
                .message("Signup successful")
                .build());
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        
        if (user.getIsBanned()) {
            throw new IllegalArgumentException("Account is banned");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .token(token)
                .planType(user.getPlanType())
                .message("Login successful")
                .build());
    }
    
    @PostMapping("/oauth/instagram/callback")
    public ResponseEntity<AuthResponse> instagramCallback(@RequestParam String code) {
        // In production, exchange code for access token
        // For now, return mock response
        
        String instagramId = "123456";
        String name = "User Name";
        String profilePictureUrl = "https://example.com/pic.jpg";
        String username = "username";
        String accessToken = "instagram_token";
        
        User user = userService.registerOrUpdateOAuthUser(instagramId, name, profilePictureUrl, username, accessToken);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .token(token)
                .planType(user.getPlanType())
                .isNewUser(user.getPhotos().isEmpty())
                .message("OAuth login successful")
                .build());
    }
}
