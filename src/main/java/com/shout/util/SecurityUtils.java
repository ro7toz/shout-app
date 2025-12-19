package com.shout.util;

import com.shout.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class for security and authentication operations
 */
@Component
public class SecurityUtils {
    
    /**
     * Get current authenticated user from security context
     */
    public static Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return Optional.of((User) principal);
        }
        
        return Optional.empty();
    }
    
    /**
     * Get current user ID
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }
    
    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    /**
     * Check if current user is Pro
     */
    public static boolean isProUser() {
        return getCurrentUser()
            .map(user -> "PRO".equals(user.getPlanType()))
            .orElse(false);
    }
    
    /**
     * Get user's daily request limit based on plan
     */
    public static Integer getDailyRequestLimit() {
        return isProUser() ? 50 : 10;
    }
    
    /**
     * Get allowed repost types based on plan
     */
    public static String[] getAllowedRepostTypes() {
        if (isProUser()) {
            return new String[]{"story", "post", "reel"};
        }
        return new String[]{"story"};
    }
}
