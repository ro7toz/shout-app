package com.shout.service;

import com.shout.model.ShoutoutExchange;
import com.shout.repository.ShoutoutExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled Tasks Service - Handles periodic background tasks
 * - Daily request counter reset (UTC midnight)
 * - Exchange expiration check (every minute)
 * - Expiration reminders (every 5 minutes)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasksService {
   
    private final UserService userService;
    private final ShoutoutExchangeService exchangeService;
    private final ShoutoutExchangeRepository exchangeRepository;
    private final NotificationService notificationService;
   
    /**
     * Reset daily request counters at midnight UTC
     * Runs at: 00:00:00 UTC every day
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    @Transactional
    public void resetDailyCounters() {
        try {
            log.info("=== Starting daily counter reset ===");
            userService.resetDailyCounters();
            log.info("=== Completed daily counter reset ===");
        } catch (Exception e) {
            log.error("Error resetting daily counters", e);
        }
    }
   
    /**
     * Process expired exchanges and apply strikes
     * Runs every minute to catch expiring exchanges
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    @Transactional
    public void processExpiredExchanges() {
        try {
            LocalDateTime now = LocalDateTime.now();
           
            // Find exchanges that have expired
            List<ShoutoutExchange> expiredExchanges = exchangeRepository
                .findByExpiresAtBeforeAndStatusNot(
                    now,
                    ShoutoutExchange.ExchangeStatus.COMPLETED
                );
           
            if (!expiredExchanges.isEmpty()) {
                log.info("Found {} expired exchanges", expiredExchanges.size());
               
                for (ShoutoutExchange exchange : expiredExchanges) {
                    processExpiredExchange(exchange);
                }
            }
           
        } catch (Exception e) {
            log.error("Error processing expired exchanges", e);
        }
    }
   
    /**
     * Send expiration reminders when 2 hours left
     * Runs every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    @Transactional
    public void sendExpirationReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoff = now.plusHours(2);
           
            // Find exchanges expiring in next 2 hours
            List<ShoutoutExchange> expiringSoon = exchangeRepository
                .findByExpiresAtBetweenAndStatusNot(
                    now,
                    cutoff,
                    ShoutoutExchange.ExchangeStatus.COMPLETED
                );
           
            if (!expiringSoon.isEmpty()) {
                log.info("Found {} exchanges expiring soon", expiringSoon.size());
               
                for (ShoutoutExchange exchange : expiringSoon) {
                    sendExpirationReminder(exchange);
                }
            }
           
        } catch (Exception e) {
            log.error("Error sending expiration reminders", e);
        }
    }
   
    /**
     * Process a single expired exchange
     * Apply strike to user who didn't post
     */
    private void processExpiredExchange(ShoutoutExchange exchange) {
        try {
            log.info("Processing expired exchange: {}", exchange.getId());
           
            // If requester didn't post
            if (!exchange.getRequesterPosted()) {
                // TODO: Apply strike to requester
                log.warn("Requester {} did not post for exchange {}",
                    exchange.getRequester().getUsername(), exchange.getId());
            }
           
            // If acceptor didn't post
            if (!exchange.getAcceptorPosted()) {
                // TODO: Apply strike to acceptor
                log.warn("Acceptor {} did not post for exchange {}",
                    exchange.getAcceptor().getUsername(), exchange.getId());
            }
           
            // Mark exchange as expired/incomplete
            exchange.setStatus(ShoutoutExchange.ExchangeStatus.INCOMPLETE);
            exchangeRepository.save(exchange);
           
            log.info("Exchange {} marked as INCOMPLETE", exchange.getId());
           
        } catch (Exception e) {
            log.error("Error processing expired exchange {}", exchange.getId(), e);
        }
    }
   
    /**
     * Send reminder notification that exchange expires soon
     */
    private void sendExpirationReminder(ShoutoutExchange exchange) {
        try {
            // Only send if not both posted yet
            if (exchange.getRequesterPosted() && exchange.getAcceptorPosted()) {
                return;
            }
           
            long hoursRemaining = java.time.temporal.ChronoUnit.HOURS
                .between(LocalDateTime.now(), exchange.getExpiresAt());
           
            // Send to requester if they haven't posted
            if (!exchange.getRequesterPosted()) {
                String message = String.format(
                    "Your exchange with %s expires in %d hours. Please post now!",
                    exchange.getAcceptor().getUsername(),
                    hoursRemaining
                );
                notificationService.createNotification(
                    exchange.getRequester(),
                    "Exchange Expiring Soon",
                    message,
                    null
                );
            }
           
            // Send to acceptor if they haven't posted
            if (!exchange.getAcceptorPosted()) {
                String message = String.format(
                    "Your exchange with %s expires in %d hours. Please post now!",
                    exchange.getRequester().getUsername(),
                    hoursRemaining
                );
                notificationService.createNotification(
                    exchange.getAcceptor(),
                    "Exchange Expiring Soon",
                    message,
                    null
                );
            }
           
            log.debug("Expiration reminder sent for exchange {}", exchange.getId());
           
        } catch (Exception e) {
            log.error("Error sending expiration reminder for exchange {}", exchange.getId(), e);
        }
    }
}