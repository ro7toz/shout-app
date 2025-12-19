package com.shoutx.service;

import com.shoutx.model.*;
import com.shoutx.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private static final String SENDER_EMAIL = "tushkinit@gmail.com";

    public void notifyShoutoutRequest(User sender, User receiver, ShoutoutExchange exchange) {
        // Create in-app notification
        Notification notification = Notification.builder()
                .user(receiver)
                .type(Notification.NotificationType.SHOUTOUT_REQUEST)
                .title(sender.getName() + " sent you a shoutout request")
                .message("User " + sender.getName() + " wants to exchange shoutouts. Accept their request?")
                .relatedUserId(String.valueOf(sender.getId()))
                .relatedExchangeId(String.valueOf(exchange.getId()))
                .build();

        notificationRepository.save(notification);

        // Send email notification
        sendEmailNotification(
                receiver.getEmail(),
                sender.getName() + " sent you a shoutout request",
                "Hi " + receiver.getName() + ",\n\n" +
                        sender.getName() + " (@" + sender.getUsername() + ") wants to exchange shoutouts with you. \n\n" +
                        "Check your dashboard to accept or decline this request.\n\n" +
                        "Best regards,\nShoutX Team"
        );
    }

    public void notifyRepostCompleted(User user, ShoutoutExchange exchange) {
        User otherUser = user.getId().equals(exchange.getSender().getId()) ? 
                exchange.getReceiver() : exchange.getSender();

        Notification notification = Notification.builder()
                .user(otherUser)
                .type(Notification.NotificationType.SHOUTOUT_REPOSTED)
                .title(user.getName() + " reposted your shoutout")
                .message("Your shoutout from " + user.getName() + " has been reposted successfully!")
                .relatedUserId(String.valueOf(user.getId()))
                .relatedExchangeId(String.valueOf(exchange.getId()))
                .build();

        notificationRepository.save(notification);

        // Send email
        sendEmailNotification(
                otherUser.getEmail(),
                user.getName() + " reposted your shoutout",
                "Hi " + otherUser.getName() + ",\n\n" +
                        user.getName() + " has successfully reposted your shoutout! \n\n" +
                        "Thanks for using ShoutX!\n\n" +
                        "Best regards,\nShoutX Team"
        );
    }

    public void notifyAccountBanned(User user) {
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.ACCOUNT_BANNED)
                .title("Your account has been banned")
                .message("Your account has received 3 strikes and has been permanently banned from ShoutX.")
                .build();

        notificationRepository.save(notification);

        sendEmailNotification(
                user.getEmail(),
                "Your ShoutX account has been banned",
                "Hi " + user.getName() + ",\n\n" +
                        "We regret to inform you that your ShoutX account has been banned due to repeated violations of our community guidelines. \n\n" +
                        "This action is permanent, and your associated Instagram account (@" + user.getUsername() + ") cannot be used to sign up again. \n\n" +
                        "If you believe this is an error, please contact us at support@shoutx.app\n\n" +
                        "Best regards,\nShoutX Team"
        );
    }

    public void notifyPaymentSuccess(User user, Payment payment) {
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.PAYMENT_SUCCESSFUL)
                .title("Payment successful - Plan upgraded")
                .message("Your payment has been processed. Your plan has been upgraded to " + payment.getPlanType())
                .build();

        notificationRepository.save(notification);

        String planDetails = payment.getPlanType() == Payment.PlanType.PRO ? 
                "50 requests per day, All media types, Advanced analytics" : 
                "10 requests per day, Stories only, Basic features";

        sendEmailNotification(
                user.getEmail(),
                "Payment successful - Welcome to " + payment.getPlanType() + " plan",
                "Hi " + user.getName() + ",\n\n" +
                        "Your payment has been successfully processed! \n\n" +
                        "Plan Details: " + planDetails + "\n" +
                        "Valid until: " + payment.getExpiresAt() + "\n\n" +
                        "Enjoy using ShoutX!\n\n" +
                        "Best regards,\nShoutX Team"
        );
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAsRead(Notification notification) {
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    private void sendEmailNotification(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the operation
            e.printStackTrace();
        }
    }
}
