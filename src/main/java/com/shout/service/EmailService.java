package com.shout.service;

import com.shout.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@shoutx.co.in}")
    private String fromEmail;

    @Value("${app.name:ShoutX}")
    private String appName;

    @Value("${app.url:https://shoutx.co.in}")
    private String appUrl;

    /**
     * Send shoutout request notification
     */
    @Async
    public void sendShoutoutRequestNotification(User requester, User target, ShoutoutRequest request) {
        try {
            String subject = "New Shoutout Request from " + requester.getFullName();
            String content = buildShoutoutRequestEmail(requester, request);
            sendHtmlEmail(target.getEmail(), subject, content);
            log.info("Shoutout request email sent to {}", target.getEmail());
        } catch (Exception e) {
            log.error("Error sending shoutout request email to {}", target.getEmail(), e);
        }
    }

    /**
     * Send shoutout acceptance notification
     */
    @Async
    public void sendShoutoutAcceptedNotification(User acceptor, ShoutoutExchange exchange) {
        try {
            String subject = "Your Shoutout Request Was Accepted!";
            String content = buildShoutoutAcceptedEmail(acceptor, exchange);
            sendHtmlEmail(exchange.getRequester().getEmail(), subject, content);
            log.info("Shoutout accepted email sent to {}", exchange.getRequester().getEmail());
        } catch (Exception e) {
            log.error("Error sending shoutout accepted email", e);
        }
    }

    /**
     * Send 24-hour timer reminder
     */
    @Async
    public void send24HourReminderEmail(User user, ShoutoutExchange exchange) {
        try {
            String subject = "[REMINDER] You have 24 hours to post your shoutout";
            String content = build24HourReminderEmail(user, exchange);
            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("24-hour reminder email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending 24-hour reminder email", e);
        }
    }

    /**
     * Send exchange completion notification
     */
    @Async
    public void sendExchangeCompletedNotification(ShoutoutExchange exchange) {
        try {
            String subject = "Shoutout Exchange Completed!";
            String content = buildExchangeCompletedEmail(exchange);
            sendHtmlEmail(exchange.getRequester().getEmail(), subject, content);
            sendHtmlEmail(exchange.getAcceptor().getEmail(), subject, content);
            log.info("Exchange completed emails sent to both users");
        } catch (Exception e) {
            log.error("Error sending exchange completion email", e);
        }
    }

    /**
     * Send failed exchange notification with violation warning
     */
    @Async
    public void sendExchangeFailedNotification(ShoutoutExchange exchange, User violator, String reason) {
        try {
            String subject = "Shoutout Exchange Failed - Strike Added";
            String content = buildExchangeFailedEmail(exchange, violator, reason);
            sendHtmlEmail(violator.getEmail(), subject, content);
            log.info("Exchange failed email sent to {}", violator.getEmail());
        } catch (Exception e) {
            log.error("Error sending exchange failed email", e);
        }
    }

    /**
     * Send account banned notification
     */
    @Async
    public void sendAccountBannedNotification(User user) {
        try {
            String subject = "Your Account Has Been Banned";
            String content = buildAccountBannedEmail(user);
            sendHtmlEmail(user.getEmail(), subject, content);
            log.warn("Account banned email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending account banned email", e);
        }
    }

    /**
     * Send subscription upgrade confirmation
     */
    @Async
    public void sendSubscriptionUpgradeEmail(User user, Subscription subscription) {
        try {
            String subject = "Welcome to ShoutX Pro!";
            String content = buildSubscriptionUpgradeEmail(user, subscription);
            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("Subscription upgrade email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending subscription upgrade email", e);
        }
    }

    /**
     * Send rating request notification
     */
    @Async
    public void sendRatingRequestNotification(User rater, User ratee, ShoutoutExchange exchange) {
        try {
            String subject = "Rate Your Shoutout Exchange with " + rater.getFullName();
            String content = buildRatingRequestEmail(rater, exchange);
            sendHtmlEmail(ratee.getEmail(), subject, content);
            log.info("Rating request email sent to {}", ratee.getEmail());
        } catch (Exception e) {
            log.error("Error sending rating request email", e);
        }
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    /**
     * Build shoutout request email HTML
     */
    private String buildShoutoutRequestEmail(User requester, ShoutoutRequest request) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>\n" +
            "<h2 style='color: #2c3e50;'>New Shoutout Request! 🎉</h2>\n" +
            "<p>Hey there!</p>\n" +
            "<p><strong>" + requester.getFullName() + "</strong> (@" + requester.getUsername() + ") would like a shoutout for their post:</p>\n" +
            "<p><a href='" + request.getPostLink() + "' style='color: #3498db; text-decoration: none;'>View their post →</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/shoutouts/" + request.getId() + "' style='display: inline-block; background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>View Request</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build shoutout accepted email HTML
     */
    private String buildShoutoutAcceptedEmail(User acceptor, ShoutoutExchange exchange) {
        LocalDateTime expiresAt = exchange.getExpiresAt();
        String formattedTime = expiresAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));

        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>\n" +
            "<h2 style='color: #27ae60;'>Shoutout Accepted! ✅</h2>\n" +
            "<p>Great news! <strong>" + acceptor.getFullName() + "</strong> has accepted your shoutout request.</p>\n" +
            "<p><strong>Important:</strong> You both now have <strong>24 hours</strong> to post the shoutout on your Instagram accounts.</p>\n" +
            "<p><strong>Deadline:</strong> " + formattedTime + "</p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/shoutouts/" + exchange.getId() + "' style='display: inline-block; background-color: #27ae60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>View Exchange Details</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build 24-hour reminder email
     */
    private String build24HourReminderEmail(User user, ShoutoutExchange exchange) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e74c3c; border-radius: 8px; background-color: #fdeaea;'>\n" +
            "<h2 style='color: #e74c3c;'>⏰ 24-Hour Reminder</h2>\n" +
            "<p>You have <strong>24 hours</strong> to post this shoutout on your Instagram Stories!</p>\n" +
            "<p>Post URL: <a href='" + exchange.getPostUrl() + "'>" + exchange.getPostUrl() + "</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/shoutouts/" + exchange.getId() + "/post' style='display: inline-block; background-color: #e74c3c; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Mark as Posted</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build exchange completed email
     */
    private String buildExchangeCompletedEmail(ShoutoutExchange exchange) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>\n" +
            "<h2 style='color: #27ae60;'>Shoutout Complete! 🎉</h2>\n" +
            "<p>Congratulations! Both shoutouts have been successfully posted.</p>\n" +
            "<p>You can now rate each other to build your reputation.</p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/ratings/" + exchange.getId() + "' style='display: inline-block; background-color: #f39c12; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Rate Exchange</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build exchange failed email
     */
    private String buildExchangeFailedEmail(ShoutoutExchange exchange, User violator, String reason) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e74c3c; border-radius: 8px; background-color: #fdeaea;'>\n" +
            "<h2 style='color: #e74c3c;'>⚠️ Exchange Failed</h2>\n" +
            "<p>Unfortunately, the shoutout exchange with <strong>" + exchange.getRequester().getUsername() + "</strong> has failed.</p>\n" +
            "<p><strong>Reason:</strong> " + reason + "</p>\n" +
            "<p><strong>Strike Added:</strong> You now have " + violator.getStrikeCount() + "/3 strikes.</p>\n" +
            "<p style='font-size: 14px; color: #c0392b;'><strong>Warning:</strong> 3 strikes will result in a permanent account ban.</p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build account banned email
     */
    private String buildAccountBannedEmail(User user) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 2px solid #c0392b; border-radius: 8px; background-color: #fadbd8;'>\n" +
            "<h2 style='color: #c0392b;'>❌ Account Banned</h2>\n" +
            "<p>Your account has been permanently banned due to repeated violations of our terms of service.</p>\n" +
            "<p><strong>Reason:</strong> You have accumulated 3 strikes by failing to honor shoutout exchanges.</p>\n" +
            "<p><strong>Next Steps:</strong> You can no longer log in with this social account. To appeal, please contact support at tushkinit@gmail.com.</p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build subscription upgrade email
     */
    private String buildSubscriptionUpgradeEmail(User user, Subscription subscription) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>\n" +
            "<h2 style='color: #9b59b6;'>Welcome to ShoutX Pro! 👑</h2>\n" +
            "<p>Congratulations! You now have access to advanced features:</p>\n" +
            "<ul style='color: #27ae60;'>\n" +
            "<li>✅ Posts & Reels support</li>\n" +
            "<li>✅ Advanced analytics with detailed metrics</li>\n" +
            "<li>✅ Monthly & media-type filters</li>\n" +
            "<li>✅ Engagement rate tracking</li>\n" +
            "<li>✅ Premium support</li>\n" +
            "</ul>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/dashboard' style='display: inline-block; background-color: #9b59b6; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Go to Dashboard</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build rating request email
     */
    private String buildRatingRequestEmail(User rater, ShoutoutExchange exchange) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>\n" +
            "<h2 style='color: #f39c12;'>Rate Your Exchange ⭐</h2>\n" +
            "<p>Help us build a trustworthy community! Please rate your exchange with <strong>" + rater.getFullName() + "</strong>.</p>\n" +
            "<p>Your rating will help other creators know what to expect.</p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p><a href='" + appUrl + "/ratings/" + exchange.getId() + "' style='display: inline-block; background-color: #f39c12; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Rate Now</a></p>\n" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>\n" +
            "<p style='font-size: 12px; color: #7f8c8d;'>© " + appName + " 2025. All rights reserved.</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }
}
