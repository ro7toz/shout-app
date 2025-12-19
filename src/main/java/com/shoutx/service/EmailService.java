package com.shoutx.service;

import com.shoutx.entity.User;
import com.shoutx.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    @Value("${email.from}")
    private String fromEmail;
    
    // In production, inject SendGrid or AWS SES client
    
    public void sendNewRequestEmail(User receiver, User sender) {
        String subject = sender.getName() + " sent you a shoutout request on ShoutX";
        String body = buildNewRequestEmailBody(sender.getName(), sender.getUsername());
        sendEmail(receiver.getEmail(), subject, body);
        log.info("New request email sent to {}", receiver.getEmail());
    }
    
    public void sendCompletionEmail(User sender, User receiver) {
        String subject = "Shoutout exchange completed on ShoutX";
        String body = buildCompletionEmailBody(receiver.getName());
        
        sendEmail(sender.getEmail(), subject, body);
        sendEmail(receiver.getEmail(), subject, body);
        log.info("Completion emails sent");
    }
    
    public void sendStrikeWarningEmail(User user, Integer strikeCount) {
        String subject = "Strike warning: " + strikeCount + "/3";
        String body = buildStrikeWarningEmailBody(strikeCount);
        sendEmail(user.getEmail(), subject, body);
        log.info("Strike warning email sent to {}", user.getEmail());
    }
    
    public void sendBanNotificationEmail(User user) {
        String subject = "Your ShoutX account has been suspended";
        String body = buildBanNotificationEmailBody();
        sendEmail(user.getEmail(), subject, body);
        log.info("Ban notification email sent to {}", user.getEmail());
    }
    
    public void sendPaymentSuccessEmail(User user, Payment payment) {
        String subject = "Pro plan activated - ShoutX";
        String body = buildPaymentSuccessEmailBody();
        sendEmail(user.getEmail(), subject, body);
        log.info("Payment success email sent to {}", user.getEmail());
    }
    
    private void sendEmail(String to, String subject, String body) {
        // In production, use SendGrid or AWS SES
        log.info("Email would be sent to: {} with subject: {}", to, subject);
    }
    
    private String buildNewRequestEmailBody(String senderName, String senderUsername) {
        return String.format(
            "<h2>New Shoutout Request</h2>" +
            "<p>Hi,</p>" +
            "<p>%s (@%s) sent you a shoutout request on ShoutX!</p>" +
            "<p>Visit your dashboard to accept or reject the request.</p>" +
            "<p><a href='https://shoutx.app/dashboard'>View Request</a></p>" +
            "<p>Best regards,<br/>ShoutX Team</p>",
            senderName, senderUsername
        );
    }
    
    private String buildCompletionEmailBody(String userName) {
        return "<h2>Shoutout Exchange Completed</h2><p>Hi,</p><p>The shoutout exchange has been completed successfully!</p>";
    }
    
    private String buildStrikeWarningEmailBody(Integer strikeCount) {
        return "<h2>Strike Warning</h2><p>You have " + strikeCount + "/3 strikes. Be careful!</p>";
    }
    
    private String buildBanNotificationEmailBody() {
        return "<h2>Account Suspended</h2><p>Your account has been suspended due to violations.</p>";
    }
    
    private String buildPaymentSuccessEmailBody() {
        return "<h2>Pro Plan Activated</h2><p>Your Pro plan is now active. Enjoy unlimited features!</p>";
    }
}
