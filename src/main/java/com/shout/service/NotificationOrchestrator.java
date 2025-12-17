package com.shout.service;

import com.shout.model.*;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationOrchestrator {
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Trigger all notifications for shoutout request
     */
    @Transactional
    public void notifyShoutoutRequest(ShoutoutRequest request) {
        try {
            // In-app notification
            notificationService.createNotification(
                request.getTarget(),
                "Shoutout Request",
                request.getRequester().getFullName() + " requested a shoutout",
                "/shoutouts/" + request.getId()
            );

            // Email notification (async)
            emailService.sendShoutoutRequestNotification(
                request.getRequester(),
                request.getTarget(),
                request
            );

            log.info("Notifications sent for shoutout request {}", request.getId());
        } catch (Exception e) {
            log.error("Error sending shoutout request notifications", e);
        }
    }

    /**
     * Trigger all notifications for shoutout acceptance
     */
    @Transactional
    public void notifyShoutoutAccepted(ShoutoutExchange exchange) {
        try {
            // In-app notification to requester
            notificationService.createNotification(
                exchange.getRequester(),
                "Shoutout Accepted!",
                exchange.getAcceptor().getFullName() + " accepted your request",
                "/shoutouts/" + exchange.getId()
            );

            // Email notification (async)
            emailService.sendShoutoutAcceptedNotification(
                exchange.getAcceptor(),
                exchange
            );

            log.info("Notifications sent for shoutout acceptance {}", exchange.getId());
        } catch (Exception e) {
            log.error("Error sending shoutout acceptance notifications", e);
        }
    }

    /**
     * Trigger exchange completion notifications
     */
    @Transactional
    public void notifyExchangeCompleted(ShoutoutExchange exchange) {
        try {
            // In-app notifications
            notificationService.createNotification(
                exchange.getRequester(),
                "Exchange Complete!",
                "Time to rate your exchange partner",
                "/ratings/" + exchange.getId()
            );

            notificationService.createNotification(
                exchange.getAcceptor(),
                "Exchange Complete!",
                "Time to rate your exchange partner",
                "/ratings/" + exchange.getId()
            );

            // Email notifications (async)
            emailService.sendExchangeCompletedNotification(exchange);

            log.info("Notifications sent for exchange completion {}", exchange.getId());
        } catch (Exception e) {
            log.error("Error sending exchange completion notifications", e);
        }
    }

    /**
     * Trigger failure notifications with strike info
     */
    @Transactional
    public void notifyExchangeFailed(ShoutoutExchange exchange, User violator, String reason) {
        try {
            // In-app notification
            notificationService.createNotification(
                violator,
                "Exchange Failed - Strike Added",
                reason + " (" + violator.getStrikeCount() + "/3 strikes)",
                "/compliance"
            );

            // Email notification (async)
            emailService.sendExchangeFailedNotification(exchange, violator, reason);

            // If banned, send ban notification
            if (violator.getAccountBanned()) {
                notifyAccountBanned(violator);
            }

            log.info("Notifications sent for exchange failure");
        } catch (Exception e) {
            log.error("Error sending exchange failure notifications", e);
        }
    }

    /**
     * Trigger account ban notification
     */
    @Transactional
    public void notifyAccountBanned(User user) {
        try {
            // Email notification (async)
            emailService.sendAccountBannedNotification(user);

            log.warn("Account ban notification sent to {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error sending account ban notification", e);
        }
    }

    /**
     * Trigger subscription upgrade confirmation
     */
    @Transactional
    public void notifySubscriptionUpgrade(User user, Subscription subscription) {
        try {
            // In-app notification
            notificationService.createNotification(
                user,
                "Welcome to ShoutX Pro!",
                "You now have access to advanced analytics",
                "/dashboard"
            );

            // Email notification (async)
            emailService.sendSubscriptionUpgradeEmail(user, subscription);

            log.info("Subscription upgrade notifications sent to {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error sending subscription upgrade notification", e);
        }
    }

    /**
     * Trigger rating request notification
     */
    @Transactional
    public void notifyRatingRequest(User rater, User ratee, ShoutoutExchange exchange) {
        try {
            // In-app notification
            notificationService.createNotification(
                ratee,
                "Rate Your Exchange",
                "Help us build a trustworthy community",
                "/ratings/" + exchange.getId()
            );

            // Email notification (async)
            emailService.sendRatingRequestNotification(rater, ratee, exchange);

            log.info("Rating request notifications sent to {}", ratee.getUsername());
        } catch (Exception e) {
            log.error("Error sending rating request notification", e);
        }
    }
}
