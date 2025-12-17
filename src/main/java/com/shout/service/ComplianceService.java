package com.shout.service;

import com.shout.model.*;
import com.shout.repository.ComplianceRecordRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceService {
    private final ComplianceRecordRepository complianceRecordRepository;
    private final UserRepository userRepository;
    private static final int MAX_STRIKES = 3;

    /**
     * Add a violation and strike to user
     */
    @Transactional
    public ComplianceRecord addViolation(User user, ShoutoutExchange exchange, 
                                        ComplianceRecord.ViolationType violationType,
                                        String description) {
        // Get current strike count
        Integer currentViolations = complianceRecordRepository.getViolationCount(user);
        int nextStrike = (currentViolations == null ? 0 : currentViolations) + 1;

        if (nextStrike > MAX_STRIKES) {
            nextStrike = MAX_STRIKES;
        }

        ComplianceRecord record = ComplianceRecord.builder()
            .user(user)
            .exchange(exchange)
            .violationType(violationType)
            .strikeNumber(nextStrike)
            .description(description)
            .accountBanned(nextStrike >= MAX_STRIKES)
            .build();

        ComplianceRecord saved = complianceRecordRepository.save(record);

        // Update user strikes
        user.setStrikeCount(nextStrike);
        
        // Ban account if 3 strikes
        if (nextStrike >= MAX_STRIKES) {
            user.setAccountBanned(true);
            user.setBannedAt(LocalDateTime.now());
            user.setSocialLoginBanned(true);
            saved.setAccountBanned(true);
            saved.setBannedAt(LocalDateTime.now());
            saved.setSocialLoginBanned(true);
            complianceRecordRepository.save(saved);
            log.warn("User {} has been banned after {} strikes", user.getUsername(), MAX_STRIKES);
        }

        userRepository.save(user);
        log.info("Violation recorded for user {}: {} (Strike {})", user.getUsername(), violationType, nextStrike);
        return saved;
    }

    /**
     * Check if user failed to post in exchange (24-hour timeout)
     */
    @Transactional
    public void checkFailedPostings(ShoutoutExchange exchange) {
        if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.EXPIRED) {
            return;
        }

        // Requester didn't post
        if (!exchange.getRequesterPosted()) {
            addViolation(
                exchange.getRequester(),
                exchange,
                ComplianceRecord.ViolationType.FAILED_TO_POST,
                "Failed to post in 24-hour window for exchange " + exchange.getId()
            );
        }

        // Acceptor didn't post
        if (!exchange.getAcceptorPosted()) {
            addViolation(
                exchange.getAcceptor(),
                exchange,
                ComplianceRecord.ViolationType.FAILED_TO_POST,
                "Failed to post in 24-hour window for exchange " + exchange.getId()
            );
        }
    }

    /**
     * Check if user removed their post after posting
     */
    @Transactional
    public void checkPostRemovalViolation(ShoutoutExchange exchange, boolean requesterRemoved) {
        User violator = requesterRemoved ? exchange.getRequester() : exchange.getAcceptor();
        
        addViolation(
            violator,
            exchange,
            ComplianceRecord.ViolationType.REMOVED_POST,
            (requesterRemoved ? "Requester" : "Acceptor") + " removed posted content"
        );
        
        if (requesterRemoved) {
            exchange.setRequesterRemoved(true);
            exchange.setRequesterRemovedAt(LocalDateTime.now());
        } else {
            exchange.setAcceptorRemoved(true);
            exchange.setAcceptorRemovedAt(LocalDateTime.now());
        }
    }

    /**
     * Get user's compliance record
     */
    public List<ComplianceRecord> getUserViolations(User user) {
        return complianceRecordRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get violation count for user
     */
    public Integer getUserViolationCount(User user) {
        Integer count = complianceRecordRepository.getViolationCount(user);
        return count != null ? count : 0;
    }

    /**
     * Check if user is banned
     */
    public boolean isUserBanned(User user) {
        return user.getAccountBanned() != null && user.getAccountBanned();
    }

    /**
     * Check if social login is banned
     */
    public boolean isSocialLoginBanned(User user) {
        return user.getSocialLoginBanned() != null && user.getSocialLoginBanned();
    }

    /**
     * Get banned users (for admin review)
     */
    public List<ComplianceRecord> getBannedUsers() {
        return complianceRecordRepository.findByAccountBanned(true);
    }

    /**
     * Clear strikes for user (admin action)
     */
    @Transactional
    public void clearUserStrikes(User user) {
        user.setStrikeCount(0);
        user.setAccountBanned(false);
        userRepository.save(user);
        log.info("Strikes cleared for user: {}", user.getUsername());
    }
}
