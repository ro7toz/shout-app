package com.shout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;

/**
 * DailyLimitService enforces the daily exchange limits.
 * Business Rules:
 * - BASIC plan: 10 send/accept per day
 * - PRO plan: 50 send/accept per day
 * - Limit is per calendar day (00:00 to 23:59)
 * - Both SEND and ACCEPT count toward the same limit
 */
@Service
@Slf4j
@Transactional
public class DailyLimitService {

    private static final int BASIC_DAILY_LIMIT = 10;
    private static final int PRO_DAILY_LIMIT = 50;

    /**
     * Checks if user has reached their daily exchange limit.
     * Counts all exchanges created/accepted by user today.
     *
     * @param userId User ID
     * @param subscriptionType Subscription type (BASIC or PRO)
     * @return true if limit reached, false if user can continue
     */
    public boolean hasReachedDailyLimit(Long userId, String subscriptionType) {
        log.debug("Checking daily limit for user {} with plan {}", userId, subscriptionType);
        
        // Get today's date range
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        // Count exchanges created/accepted by this user today
        // This counts BOTH send and accept
        // long todayExchangeCount = exchangeRepository
        //     .countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
        // OR for more precise:
        // long todayExchangeCount = exchangeRepository
        //     .countByUserIdAndCreatedAtAfter(userId, startOfDay);
        long todayExchangeCount = 0; // Placeholder
        
        // Determine limit based on plan
        int dailyLimit = "PRO".equalsIgnoreCase(subscriptionType) 
            ? PRO_DAILY_LIMIT 
            : BASIC_DAILY_LIMIT;
        
        boolean limitReached = todayExchangeCount >= dailyLimit;
        
        if (limitReached) {
            log.warn("User {} reached daily limit ({}/{}) for {} plan", 
                userId, todayExchangeCount, dailyLimit, subscriptionType);
        } else {
            log.debug("User {} has ({}/{}) exchanges today for {} plan", 
                userId, todayExchangeCount, dailyLimit, subscriptionType);
        }
        
        return limitReached;
    }

    /**
     * Gets remaining exchanges for the day.
     *
     * @param userId User ID
     * @param subscriptionType Subscription type (BASIC or PRO)
     * @return Number of remaining exchanges user can create/accept
     */
    public int getRemainingExchanges(Long userId, String subscriptionType) {
        log.debug("Getting remaining exchanges for user {} with plan {}", userId, subscriptionType);
        
        // Get today's date range
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        // Count exchanges created/accepted by this user today
        // long todayExchangeCount = exchangeRepository
        //     .countByUserIdAndCreatedAtAfter(userId, startOfDay);
        long todayExchangeCount = 0; // Placeholder
        
        // Determine limit based on plan
        int dailyLimit = "PRO".equalsIgnoreCase(subscriptionType) 
            ? PRO_DAILY_LIMIT 
            : BASIC_DAILY_LIMIT;
        
        int remaining = (int) (dailyLimit - todayExchangeCount);
        return Math.max(0, remaining);
    }

    /**
     * Gets daily exchange count for a user.
     *
     * @param userId User ID
     * @return Number of exchanges created/accepted today
     */
    public long getTodayExchangeCount(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        // Count exchanges
        // return exchangeRepository.countByUserIdAndCreatedAtAfter(userId, startOfDay);
        return 0L; // Placeholder
    }

    /**
     * Gets the daily limit for a subscription type.
     *
     * @param subscriptionType Subscription type (BASIC or PRO)
     * @return Daily limit (10 for BASIC, 50 for PRO)
     */
    public int getDailyLimit(String subscriptionType) {
        return "PRO".equalsIgnoreCase(subscriptionType) 
            ? PRO_DAILY_LIMIT 
            : BASIC_DAILY_LIMIT;
    }
}
