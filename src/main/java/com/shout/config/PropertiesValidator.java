package com.shout.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validates critical application properties on startup
 * Fails fast if configuration is incorrect
 */
@Component
@Slf4j
public class PropertiesValidator {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username:}")
    private String dbUsername;

    @EventListener(ApplicationReadyEvent.class)
    public void validateProperties() {
        log.info("========================================");
        log.info("Validating application properties...");
        log.info("========================================");

        boolean hasErrors = false;

        // Validate JWT Secret
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            log.error("❌ JWT secret is not configured!");
            hasErrors = true;
        } else if (jwtSecret.length() < 32) {
            log.error("❌ JWT secret must be at least 32 characters long (current: {})", jwtSecret.length());
            hasErrors = true;
        } else {
            log.info("✓ JWT secret configured (length: {})", jwtSecret.length());
        }

        // Validate Database URL
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            log.error("❌ Database URL is not configured!");
            hasErrors = true;
        } else {
            if (!dbUrl.contains("shoutx")) {
                log.warn("⚠️  Database URL doesn't contain 'shoutx' - verify database name");
            }
            log.info("✓ Database URL: {}", dbUrl.replaceAll("password=[^&]*", "password=***"));
        }
        
        // Validate Database Username
        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            log.warn("⚠️  Database username is not configured");
        } else {
            log.info("✓ Database username: {}", dbUsername);
        }

        // Check for default/insecure values
        if (jwtSecret.contains("change-this") || jwtSecret.contains("your-secret")) {
            log.warn("⚠️  JWT secret appears to be a default value - change in production!");
        }

        log.info("========================================");
        
        if (hasErrors) {
            log.error("❌ Application configuration has ERRORS - please fix before running");
            throw new IllegalStateException("Invalid application configuration - check logs for details");
        } else {
            log.info("✓ All critical properties validated successfully");
        }
        
        log.info("========================================");
    }
}
