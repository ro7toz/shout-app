package com.shout.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// ✅ FIXED: Changed from javax to jakarta
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Enhanced JWT Token Provider - Includes plan type in token claims
 * Extends JwtTokenProvider with additional functionality
 */
@Component("jwtTokenProviderEnhanced")
@Slf4j
public class JwtTokenProviderEnhanced {
 
    @Value("${jwt.secret}")
    private String jwtSecret;
 
    @Value("${jwt.expiration:86400000}") // 24 hours default
    private long jwtExpirationMs;
 
    /**
     * Generate JWT token with plan type information
     */
    public String generateTokenWithPlan(Long userId, String planType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
 
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
 
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("planType", planType) // Include plan in token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
 
    /**
     * Extract plan type from JWT token
     */
    public String getPlanTypeFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("planType", String.class);
        } catch (Exception e) {
            log.error("Error extracting plan type from token", e);
            return "BASIC"; // Default to BASIC if not found
        }
    }
 
    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
           
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
 
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.error("Error extracting user ID from token", e);
            return null;
        }
    }
 
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
           
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
           
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
 
    /**
     * Extract JWT token from HTTP request
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
       
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
       
        return null;
    }
 
    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}
