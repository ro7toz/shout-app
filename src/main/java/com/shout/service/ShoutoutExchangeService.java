package com.shout.service;

import com.shout.model.*;
import com.shout.repository.PostAnalyticsRepository;
import com.shout.repository.ShoutoutExchangeRepository;
import com.shout.repository.ShoutoutRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoutoutExchangeService {
    private final ShoutoutExchangeRepository exchangeRepository;
    private final ShoutoutRequestRepository requestRepository;
    private final PostAnalyticsRepository analyticsRepository;
    private final ComplianceService complianceService;
    private static final int EXCHANGE_WINDOW_HOURS = 24;

    /**
     * Create exchange from accepted request
     */
    @Transactional
    public ShoutoutExchange createExchange(ShoutoutRequest request) {
        if (request.getStatus() != ShoutoutRequest.RequestStatus.ACCEPTED) {
            throw new RuntimeException("Request must be accepted first");
        }

        ShoutoutExchange exchange = ShoutoutExchange.builder()
            .requester(request.getRequester())
            .acceptor(request.getTarget())
            .shoutoutRequest(request)
            .postUrl(request.getPostLink())
            .expiresAt(LocalDateTime.now().plusHours(EXCHANGE_WINDOW_HOURS))
            .status(ShoutoutExchange.ExchangeStatus.PENDING)
            .requesterPosted(false)
            .acceptorPosted(false)
            .build();

        ShoutoutExchange saved = exchangeRepository.save(exchange);
        log.info("Exchange created: {} <-> {}", request.getRequester().getUsername(), request.getTarget().getUsername());
        return saved;
    }

    /**
     * Mark requester as posted
     */
    @Transactional
    public void markRequesterPosted(ShoutoutExchange exchange, String postUrl) {
        if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.PENDING) {
            throw new RuntimeException("Exchange is not pending");
        }

        exchange.setRequesterPosted(true);
        exchange.setRequesterPostedAt(LocalDateTime.now());
        exchange.setRequesterPostUrl(postUrl);
        exchangeRepository.save(exchange);

        checkExchangeCompletion(exchange);
        log.info("Requester marked as posted for exchange {}", exchange.getId());
    }

    /**
     * Mark acceptor as posted
     */
    @Transactional
    public void markAcceptorPosted(ShoutoutExchange exchange, String postUrl) {
        if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.PENDING) {
            throw new RuntimeException("Exchange is not pending");
        }

        exchange.setAcceptorPosted(true);
        exchange.setAcceptorPostedAt(LocalDateTime.now());
        exchange.setAcceptorPostUrl(postUrl);
        exchangeRepository.save(exchange);

        checkExchangeCompletion(exchange);
        log.info("Acceptor marked as posted for exchange {}", exchange.getId());
    }

    /**
     * Check if exchange is complete (both posted)
     */
    @Transactional
    public void checkExchangeCompletion(ShoutoutExchange exchange) {
        if (exchange.getRequesterPosted() && exchange.getAcceptorPosted()) {
            exchange.setStatus(ShoutoutExchange.ExchangeStatus.COMPLETED);
            exchangeRepository.save(exchange);
            log.info("Exchange {} marked as COMPLETED", exchange.getId());
        }
    }

    /**
     * Detect and handle post removal violations
     */
    @Transactional
    public void detectPostRemoval(ShoutoutExchange exchange) {
        // Check if requester removed post
        if (exchange.getRequesterPosted() && !isPostStillActive(exchange.getRequesterPostUrl())) {
            complianceService.checkPostRemovalViolation(exchange, true);
        }

        // Check if acceptor removed post
        if (exchange.getAcceptorPosted() && !isPostStillActive(exchange.getAcceptorPostUrl())) {
            complianceService.checkPostRemovalViolation(exchange, false);
        }
    }

    /**
     * Check if post URL is still active (stub - implement with Instagram API)
     */
    private boolean isPostStillActive(String postUrl) {
        // TODO: Implement Instagram API call to check if post still exists
        return true;
    }

    /**
     * Scheduled task: Mark expired exchanges and apply violations
     */
    @Scheduled(fixedDelay = 60000) // Run every minute
    @Transactional
    public void processExpiredExchanges() {
        LocalDateTime now = LocalDateTime.now();
        List<ShoutoutExchange> expiredExchanges = exchangeRepository.findByExpiresAtBefore(now);

        for (ShoutoutExchange exchange : expiredExchanges) {
            if (exchange.getStatus() == ShoutoutExchange.ExchangeStatus.PENDING) {
                exchange.setStatus(ShoutoutExchange.ExchangeStatus.EXPIRED);
                exchangeRepository.save(exchange);

                // Apply compliance violations
                complianceService.checkFailedPostings(exchange);
                log.info("Exchange {} marked as EXPIRED with violations applied", exchange.getId());
            }
        }
    }

    /**
     * Get pending exchanges for user
     */
    public List<ShoutoutExchange> getPendingExchanges(User user) {
        return exchangeRepository.findPendingExchangesForUser(user);
    }

    /**
     * Get exchange by ID
     */
    public ShoutoutExchange getExchangeById(Long id) {
        return exchangeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exchange not found"));
    }
}
