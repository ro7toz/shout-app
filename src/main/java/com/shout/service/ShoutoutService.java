package com.shout.service;

import com.shout.model.*;
import com.shout.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShoutoutService {

    private final ShoutoutRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;
    private final NotificationRepository notificationRepo;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepo.findAllActiveUsers(pageable);
    }

    public Page<User> getUsersByCategory(String category, Pageable pageable) {
        return userRepo.findByCategory(category, pageable);
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepo.searchUsers(query, pageable);
    }

    public void createShoutoutRequest(String requesterUsername, String targetUsername, String postLink) {
        User requester = userRepo.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        User target = userRepo.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        ShoutoutRequest request = new ShoutoutRequest();
        request.setRequester(requester);
        request.setTarget(target);
        request.setPostLink(postLink);
        request.setStatus(ShoutoutRequest.RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        ShoutoutRequest saved = requestRepo.save(request);
        log.info("Shoutout request created: {} -> {}", requesterUsername, targetUsername);

        createNotification(target, Notification.NotificationType.REQUEST_RECEIVED,
                "New Shoutout Request",
                requester.getUsername() + " requested a shoutout exchange",
                "/dashboard/requests",
                saved);
    }

    public void acceptShoutoutRequest(Long requestId, String currentUsername) {
        ShoutoutRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getTarget().getUsername().equals(currentUsername)) {
            throw new RuntimeException("Unauthorized");
        }

        if (!request.getStatus().equals(ShoutoutRequest.RequestStatus.PENDING)) {
            throw new RuntimeException("Request is no longer pending");
        }

        request.setStatus(ShoutoutRequest.RequestStatus.ACCEPTED);
        request.setAcceptedAt(LocalDateTime.now());
        requestRepo.save(request);
        log.info("Shoutout request accepted: {}", requestId);

        createNotification(request.getRequester(), Notification.NotificationType.REQUEST_ACCEPTED,
                "Request Accepted!",
                request.getTarget().getUsername() + " accepted your shoutout request. You have 24 hours!",
                "/dashboard",
                request);
    }

    public void markAsPosted(Long requestId, String currentUsername, boolean isRequester) {
        ShoutoutRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (isRequester) {
            if (!request.getRequester().getUsername().equals(currentUsername)) {
                throw new RuntimeException("Unauthorized");
            }
            request.setRequesterPosted(true);
            request.setRequesterPostedAt(LocalDateTime.now());
        } else {
            if (!request.getTarget().getUsername().equals(currentUsername)) {
                throw new RuntimeException("Unauthorized");
            }
            request.setTargetPosted(true);
            request.setTargetPostedAt(LocalDateTime.now());
        }

        if (request.getRequesterPosted() && request.getTargetPosted()) {
            request.setStatus(ShoutoutRequest.RequestStatus.COMPLETED);
            addUsersToCircle(request.getRequester(), request.getTarget());
        }

        requestRepo.save(request);
        log.info("Request marked as posted by requester={}: {}", isRequester, requestId);
    }

    public void cancelShoutoutRequest(Long requestId, String currentUsername) {
        ShoutoutRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getRequester().getUsername().equals(currentUsername) &&
            !request.getTarget().getUsername().equals(currentUsername)) {
            throw new RuntimeException("Unauthorized");
        }

        request.setStatus(ShoutoutRequest.RequestStatus.CANCELLED);
        requestRepo.save(request);
        log.info("Shoutout request cancelled: {}", requestId);
    }

    @Scheduled(fixedRate = 3600000)
    public void checkExpiredShoutouts() {
        log.info("Running expiration check for shoutout requests");
        LocalDateTime expirationThreshold = LocalDateTime.now().minusHours(24);
        List<ShoutoutRequest> expiredRequests = requestRepo.findExpiredRequests(expirationThreshold);

        for (ShoutoutRequest request : expiredRequests) {
            if (request.getStatus().equals(ShoutoutRequest.RequestStatus.ACCEPTED)) {
                if (!request.getRequesterPosted() || !request.getTargetPosted()) {
                    request.setStatus(ShoutoutRequest.RequestStatus.FAILED);
                    request.setIsExpired(true);
                    requestRepo.save(request);

                    if (request.getRequesterPosted() && !request.getTargetPosted()) {
                        createNotification(request.getRequester(),
                                Notification.NotificationType.REQUEST_EXPIRED,
                                "Request Failed",
                                request.getTarget().getUsername() + " didn't post their part. You can now rate them.",
                                "/dashboard/rate/" + request.getTarget().getUsername(),
                                request);
                    } else if (!request.getRequesterPosted() && request.getTargetPosted()) {
                        createNotification(request.getTarget(),
                                Notification.NotificationType.REQUEST_EXPIRED,
                                "Request Failed",
                                request.getRequester().getUsername() + " didn't post their part. You can now rate them.",
                                "/dashboard/rate/" + request.getRequester().getUsername(),
                                request);
                    }

                    log.info("Marked request {} as expired and failed", request.getId());
                }
            }
        }
    }

    public void submitRating(String raterUsername, String ratedUsername, Integer score, String reason, String comment) {
        User rater = userRepo.findByUsername(raterUsername)
                .orElseThrow(() -> new RuntimeException("Rater not found"));
        User ratedUser = userRepo.findByUsername(ratedUsername)
                .orElseThrow(() -> new RuntimeException("Rated user not found"));

        Rating rating = new Rating();
        rating.setRater(rater);
        rating.setRatedUser(ratedUser);
        rating.setScore(score);
        rating.setReason(reason);
        rating.setComment(comment);
        ratingRepo.save(rating);
        log.info("Rating submitted: {} rated {} with score {}", raterUsername, ratedUsername, score);

        ratedUser.updateAverageRating();
        userRepo.save(ratedUser);

        createNotification(ratedUser,
                Notification.NotificationType.BAD_RATING,
                "New Rating Received",
                rater.getUsername() + " gave you a " + score + "-star rating",
                "/profile/" + raterUsername,
                null);
    }

    public Page<Rating> getUserRatings(String username, Pageable pageable) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepo.findByRatedUser(user, pageable);
    }

    private void addUsersToCircle(User user1, User user2) {
        if (!user1.getCircle().contains(user2)) {
            user1.getCircle().add(user2);
            userRepo.save(user1);
        }
        if (!user2.getCircle().contains(user1)) {
            user2.getCircle().add(user1);
            userRepo.save(user2);
        }

        createNotification(user1,
                Notification.NotificationType.ADDED_TO_CIRCLE,
                "Added to Circle!",
                "You and " + user2.getUsername() + " have been added to each other's circle!",
                "/circle",
                null);
        createNotification(user2,
                Notification.NotificationType.ADDED_TO_CIRCLE,
                "Added to Circle!",
                "You and " + user1.getUsername() + " have been added to each other's circle!",
                "/circle",
                null);

        log.info("Users added to circle: {} <-> {}", user1.getUsername(), user2.getUsername());
    }

    private void createNotification(User user, Notification.NotificationType type,
                                   String title, String message, String actionUrl,
                                   ShoutoutRequest request) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setRelatedRequest(request);
        notification.setIsRead(false);
        notificationRepo.save(notification);
    }

    public Page<Notification> getUserNotifications(String username, Pageable pageable) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepo.findByUser(user, pageable);
    }

    public Long getUnreadNotificationCount(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepo.countByUserAndIsReadFalse(user);
    }

    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepo.findById(notificationId);
        notification.ifPresent(n -> {
            n.setIsRead(true);
            notificationRepo.save(n);
        });
    }
}