package com.shoutx.service;

import com.shoutx.model.*;
import com.shoutx.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoutoutExchangeService {

    private final ShoutoutExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final UserStrikeRepository strikeRepository;
    private final NotificationService notificationService;

    public ShoutoutExchange createExchangeRequest(User sender, User receiver, UserMedia senderMedia) {
        // Check if sender can send requests
        if (!sender.canSendRequest()) {
            throw new RuntimeException("Daily request limit reached or account banned");
        }

        ShoutoutExchange exchange = ShoutoutExchange.builder()
                .sender(sender)
                .receiver(receiver)
                .senderMedia(senderMedia)
                .status(ShoutoutExchange.ExchangeStatus.PENDING)
                .completionStatus(ShoutoutExchange.CompletionStatus.INCOMPLETE)
                .build();

        exchange = exchangeRepository.save(exchange);
        sender.setDailySendRequestsCount(sender.getDailySendRequestsCount() + 1);
        userRepository.save(sender);

        // Send notification to receiver
        notificationService.notifyShoutoutRequest(sender, receiver, exchange);

        return exchange;
    }

    public ShoutoutExchange acceptExchangeRequest(User receiver, ShoutoutExchange exchange) {
        if (!receiver.canAcceptRequest()) {
            throw new RuntimeException("Daily accept limit reached");
        }

        if (!exchange.getReceiver().getId().equals(receiver.getId())) {
            throw new RuntimeException("Unauthorized: You are not the receiver");
        }

        exchange.setStatus(ShoutoutExchange.ExchangeStatus.ACCEPTED);
        exchange.setAcceptedAt(LocalDateTime.now());
        exchange = exchangeRepository.save(exchange);

        receiver.setDailyAcceptRequestsCount(receiver.getDailyAcceptRequestsCount() + 1);
        userRepository.save(receiver);

        return exchange;
    }

    public ShoutoutExchange completeRepost(ShoutoutExchange exchange, User user, UserMedia receiverMedia) {
        exchange.setReceiverMedia(receiverMedia);

        if (exchange.getSender().getId().equals(user.getId())) {
            exchange.setSenderReposted(true);
            exchange.setSenderRepostAt(LocalDateTime.now());
        } else if (exchange.getReceiver().getId().equals(user.getId())) {
            exchange.setReceiverReposted(true);
            exchange.setReceiverRepostAt(LocalDateTime.now());
        }

        exchange.updateCompletionStatus();
        exchange = exchangeRepository.save(exchange);

        // Notify the other party
        notificationService.notifyRepostCompleted(user, exchange);

        return exchange;
    }

    public void handleExpiredExchanges() {
        List<ShoutoutExchange> expiredExchanges = exchangeRepository.findExpiredIncompleteExchanges(LocalDateTime.now());

        for (ShoutoutExchange exchange : expiredExchanges) {
            if (!exchange.getSenderReposted() && exchange.getReceiverReposted()) {
                addStrikeToUser(exchange.getSender(), UserStrike.StrikeReason.DID_NOT_REPOST, exchange.getId());
            } else if (exchange.getSenderReposted() && !exchange.getReceiverReposted()) {
                addStrikeToUser(exchange.getReceiver(), UserStrike.StrikeReason.DID_NOT_REPOST, exchange.getId());
            }

            exchange.setCompletionStatus(ShoutoutExchange.CompletionStatus.EXPIRED);
            exchangeRepository.save(exchange);
        }
    }

    public void addStrikeToUser(User user, UserStrike.StrikeReason reason, Long exchangeId) {
        UserStrike strike = UserStrike.builder()
                .user(user)
                .reason(reason)
                .relatedExchangeId(exchangeId)
                .description("User failed to repost within 24 hours")
                .build();

        strikeRepository.save(strike);
        user.addStrike();
        userRepository.save(user);

        if (user.getStrikeCount() >= 3) {
            notificationService.notifyAccountBanned(user);
        }
    }

    public List<ShoutoutExchange> getUserExchangeHistory(User user) {
        return exchangeRepository.findAllExchangesForUser(user);
    }

    public List<ShoutoutExchange> getPendingRequests(User user) {
        return exchangeRepository.findPendingRequestsForUser(user);
    }

    public long getSuccessfulExchangeCount(User user) {
        return exchangeRepository.countSuccessfulExchanges(user);
    }
}
