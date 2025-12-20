package com.shout.service;

import com.shout.model.ShoutoutExchange;
import com.shout.model.User;
import com.shout.repository.ShoutoutExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Instagram Verification Service - Automated post verification
 * Runs every 5 minutes to check if users posted their exchanges
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramVerificationService {
   
    private final ShoutoutExchangeRepository exchangeRepository;
    private final ShoutoutExchangeService exchangeService;
   
    /**
     * Verify pending posts - runs every 5 minutes
     * Checks last 5 minutes of Instagram stories/posts for verification URLs
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    @Transactional
    public void verifyPendingPosts() {
        try {
            log.info("Starting Instagram post verification check...");
           
            List<ShoutoutExchange> pending = exchangeRepository
                .findByStatusIn(Arrays.asList(
                    ShoutoutExchange.ExchangeStatus.PENDING,
                    ShoutoutExchange.ExchangeStatus.ACCEPTED
                ));
           
            log.info("Found {} pending exchanges to verify", pending.size());
           
            for (ShoutoutExchange exchange : pending) {
                verifyExchange(exchange);
            }
           
        } catch (Exception e) {
            log.error("Error during Instagram verification check", e);
        }
    }
   
    /**
     * Verify a single exchange
     */
    private void verifyExchange(ShoutoutExchange exchange) {
        try {
            // Check requester's post
            if (!exchange.getRequesterPosted()) {
                boolean requesterVerified = checkInstagramPost(
                    exchange.getRequester(),
                    exchange.getPostUrl()
                );
                if (requesterVerified) {
                    exchangeService.markRequesterPosted(exchange, null);
                    log.info("Requester verified for exchange {}", exchange.getId());
                }
            }
           
            // Check acceptor's post
            if (!exchange.getAcceptorPosted()) {
                boolean acceptorVerified = checkInstagramPost(
                    exchange.getAcceptor(),
                    exchange.getPostUrl()
                );
                if (acceptorVerified) {
                    exchangeService.markAcceptorPosted(exchange, null);
                    log.info("Acceptor verified for exchange {}", exchange.getId());
                }
            }
           
        } catch (Exception e) {
            log.error("Error verifying exchange {}", exchange.getId(), e);
        }
    }
   
    /**
     * Check if user has posted to Instagram recently
     * TODO: Implement actual Instagram API integration
     * This is a stub implementation
     */
    private boolean checkInstagramPost(User user, String targetUrl) {
        try {
            // STUB IMPLEMENTATION
            // In production, this would:
            // 1. Use user's Instagram access token
            // 2. Call Instagram Graph API to fetch recent media
            // 3. Check if targetUrl or matching content appears in recent posts/stories
            // 4. Return true if found, false otherwise
           
            // For now, return false (verification required)
            log.debug("Checking Instagram post for user {}", user.getUsername());
            return false;
           
        } catch (Exception e) {
            log.error("Error checking Instagram post for user {}", user.getUsername(), e);
            return false;
        }
    }
}