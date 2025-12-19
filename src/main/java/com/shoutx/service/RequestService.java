package com.shoutx.service;

import com.shoutx.entity.*;
import com.shoutx.repository.RequestRepository;
import com.shoutx.repository.UserRepository;
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
public class RequestService {
    
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final StrikeService strikeService;
    private final NotificationService notificationService;
    private final UserService userService;
    
    @Transactional
    public Request createRequest(Long senderId, Long receiverId, Long photoId) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        
        if (sender.getIsBanned()) {
            throw new IllegalArgumentException("Sender account is banned");
        }
        
        if (receiver.getIsBanned()) {
            throw new IllegalArgumentException("Receiver account is banned");
        }
        
        // Check daily request limit
        if (!canMakeRequest(senderId)) {
            throw new IllegalArgumentException("Daily request limit exceeded");
        }
        
        UserPhoto photo = new UserPhoto(); // Would be fetched from DB in real implementation
        
        LocalDateTime deadline = LocalDateTime.now().plusHours(24);
        
        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .photo(photo)
                .status(Request.RequestStatus.PENDING)
                .deadline(deadline)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Request savedRequest = requestRepository.save(request);
        
        // Send notification
        notificationService.sendNewRequestNotification(receiver, sender, savedRequest);
        
        log.info("Request created: {} -> {}", senderId, receiverId);
        return savedRequest;
    }
    
    @Transactional
    public Request acceptRequest(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Only receiver can accept request");
        }
        
        if (!request.getStatus().equals(Request.RequestStatus.PENDING)) {
            throw new IllegalArgumentException("Request is not pending");
        }
        
        if (LocalDateTime.now().isAfter(request.getDeadline())) {
            throw new IllegalArgumentException("Request deadline has passed");
        }
        
        request.setStatus(Request.RequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        
        return requestRepository.save(request);
    }
    
    @Transactional
    public Request completeRequest(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        // Can be completed by either sender or receiver
        if (!request.getSender().getId().equals(userId) && !request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to complete this request");
        }
        
        if (!request.getStatus().equals(Request.RequestStatus.ACCEPTED)) {
            throw new IllegalArgumentException("Request is not accepted");
        }
        
        request.setStatus(Request.RequestStatus.COMPLETED);
        request.setRepostCompletedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        Request completedRequest = requestRepository.save(request);
        
        // Send completion notification
        notificationService.sendCompletionNotification(request.getSender(), request.getReceiver(), completedRequest);
        
        log.info("Request completed: {}", requestId);
        return completedRequest;
    }
    
    public List<Request> getPendingRequestsForUser(Long userId) {
        return requestRepository.getPendingRequestsForUser(userId);
    }
    
    public List<Request> getSentRequests(Long userId) {
        return requestRepository.findBySenderId(userId);
    }
    
    public List<Request> getReceivedRequests(Long userId) {
        return requestRepository.findByReceiverId(userId);
    }
    
    @Transactional
    public void rateExchange(Long requestId, Long userId, Integer rating, String comment) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        if (request.getSender().getId().equals(userId)) {
            request.setRatingBySender(rating);
            request.setSenderComment(comment);
        } else if (request.getReceiver().getId().equals(userId)) {
            request.setRatingByReceiver(rating);
            request.setReceiverComment(comment);
        } else {
            throw new IllegalArgumentException("User is not part of this request");
        }
        
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
        
        log.info("Request {} rated by user {} with rating {}", requestId, userId, rating);
    }
    
    public boolean canMakeRequest(Long userId) {
        User user = userService.getUserById(userId);
        int dailyLimit = user.getPlanType().equals(User.PlanType.BASIC) ? 10 : 50;
        
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime tomorrow = today.plusDays(1);
        
        long countToday = requestRepository.findBySenderId(userId).stream()
                .filter(r -> r.getCreatedAt().isAfter(today) && r.getCreatedAt().isBefore(tomorrow))
                .count();
        
        return countToday < dailyLimit;
    }
    
    // Scheduled job to handle expired requests (run daily at 00:00)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void handleExpiredRequests() {
        log.info("Handling expired requests...");
        List<Request> expiredRequests = requestRepository.getExpiredRequests(LocalDateTime.now());
        
        for (Request request : expiredRequests) {
            // Check if sender reposted
            if (request.getSenderRepostStatus() == null || !request.getSenderRepostStatus().equals(Request.RepostStatus.REPOSTED)) {
                strikeService.addStrike(request.getSender().getId(), "Failed to repost shoutout", request.getId());
            }
            
            // Check if receiver reposted
            if (request.getReceiverRepostStatus() == null || !request.getReceiverRepostStatus().equals(Request.RepostStatus.REPOSTED)) {
                strikeService.addStrike(request.getReceiver().getId(), "Failed to repost shoutout", request.getId());
            }
            
            request.setStatus(Request.RequestStatus.EXPIRED);
            requestRepository.save(request);
        }
        
        log.info("Processed {} expired requests", expiredRequests.size());
    }
}
