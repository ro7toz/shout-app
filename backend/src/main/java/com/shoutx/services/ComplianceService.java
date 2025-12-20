package com.shoutx.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * ComplianceService handles the strike system and user banning logic.
 * Business Rules:
 * - 1 Strike: User failed to repost within 24 hours
 * - 2 Strikes: Final warning notification sent
 * - 3 Strikes: Permanent account ban + Instagram ID blacklisted
 * - Strikes CANNOT be removed
 * - Banned users CANNOT create new accounts with same Instagram ID
 */
@Service
@Slf4j
@Transactional
public class ComplianceService {

    @Autowired
    private NotificationService notificationService;

    /**
     * Adds a strike to a user for violating exchange rules.
     * Implements escalating enforcement:
     * - 1st strike: Notification
     * - 2nd strike: Final warning notification + visible alert
     * - 3rd strike: Ban + blacklist Instagram ID
     *
     * @param userId User ID to add strike to
     * @param exchangeId Related exchange ID
     * @param violationType Type of violation
     * @param description Violation description
     */
    public void addStrike(Long userId, Long exchangeId, String violationType, String description) {
        log.info("Adding strike for user {} - Violation: {}", userId, violationType);
        
        // Fetch user from database
        // User user = userRepository.findById(userId).orElseThrow(...);
        
        // Increment strike count
        // user.setStrikeCount(user.getStrikeCount() + 1);
        
        // Save to database
        // userRepository.save(user);
        
        // Handle based on strike count
        // int strikes = user.getStrikeCount();
        
        // if (strikes == 1) {
        //     notificationService.sendStrikeNotification(user, 1);
        //     log.warn("User {} received 1st strike", userId);
        // }
        // else if (strikes == 2) {
        //     notificationService.sendFinalWarningNotification(user);
        //     log.warn("User {} received 2nd strike - FINAL WARNING", userId);
        // }
        // else if (strikes >= 3) {
        //     banUser(user);
        //     log.error("User {} BANNED after 3 strikes", userId);
        // }
        
        // Log compliance record
        // ComplianceRecord record = new ComplianceRecord();
        // record.setUserId(userId);
        // record.setExchangeId(exchangeId);
        // record.setViolationType(violationType);
        // record.setDescription(description);
        // record.setStrikeAdded(true);
        // complianceRecordRepository.save(record);
    }

    /**
     * Permanently bans a user (3 strikes reached).
     * This action is IRREVERSIBLE.
     *
     * @param userId User ID to ban
     */
    public void banUser(Long userId) {
        log.error("BANNING USER: {} - 3 STRIKES REACHED", userId);
        
        // Fetch user
        // User user = userRepository.findById(userId).orElseThrow(...);
        
        // Ban the user
        // user.setIsBanned(true);
        // userRepository.save(user);
        
        // Blacklist Instagram ID
        // BannedInstagramAccount bannedAccount = new BannedInstagramAccount();
        // bannedAccount.setInstagramId(user.getInstagramId());
        // bannedAccount.setBannedReason("3 STRIKES - PERMANENT BAN");
        // bannedAccount.setBannedAt(LocalDateTime.now());
        // bannedInstagramAccountRepository.save(bannedAccount);
        
        // Cancel all active exchanges
        // cancelActiveExchanges(userId);
        
        // Send permanent ban notification
        // notificationService.sendPermanentBanNotification(user);
    }

    /**
     * Checks if an Instagram ID is blacklisted.
     * Used during registration to prevent banned users from re-registering.
     *
     * @param instagramId Instagram ID to check
     * @return true if banned, false if allowed
     */
    public boolean isInstagramIdBanned(String instagramId) {
        // BannedInstagramAccount banned = bannedInstagramAccountRepository
        //     .findByInstagramId(instagramId);
        // return banned != null;
        return false; // Placeholder
    }

    /**
     * Gets strike count for a user.
     *
     * @param userId User ID
     * @return Current strike count (0-3+)
     */
    public int getStrikeCount(Long userId) {
        // User user = userRepository.findById(userId).orElseThrow(...);
        // return user.getStrikeCount();
        return 0; // Placeholder
    }

    /**
     * Checks if user is banned.
     *
     * @param userId User ID
     * @return true if banned, false otherwise
     */
    public boolean isUserBanned(Long userId) {
        // User user = userRepository.findById(userId).orElseThrow(...);
        // return user.getIsBanned();
        return false; // Placeholder
    }
}
