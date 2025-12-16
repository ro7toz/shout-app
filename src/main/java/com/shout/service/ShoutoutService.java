package com.shout.service;

import com.shout.dto.ShoutoutRequestDto;
import com.shout.exception.ResourceNotFoundException;
import com.shout.exception.UnauthorizedException;
import com.shout.model.ShoutoutRequest;
import com.shout.model.User;
import com.shout.repository.ShoutoutRequestRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ShoutoutService {
    private final ShoutoutRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ShoutoutRequest createRequest(String requesterUsername, String targetUsername, String postLink) {
        User requester = userRepository.findById(requesterUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", requesterUsername));
        
        User target = userRepository.findById(targetUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", targetUsername));

        ShoutoutRequest request = ShoutoutRequest.builder()
            .requester(requester)
            .target(target)
            .postLink(postLink)
            .status(ShoutoutRequest.RequestStatus.PENDING)
            .build();

        ShoutoutRequest saved = requestRepository.save(request);
        log.info("Request created: {} -> {}", requesterUsername, targetUsername);

        // Send notification
        notificationService.notifyRequestReceived(target, requester, saved);
        
        return saved;
    }

    public ShoutoutRequest acceptRequest(Long requestId, String targetUsername) {
        ShoutoutRequest request = requestRepository.findByIdAndTargetUsername(requestId, targetUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        request.setStatus(ShoutoutRequest.RequestStatus.ACCEPTED);
        request.setAcceptedAt(LocalDateTime.now());
        
        ShoutoutRequest saved = requestRepository.save(request);
        log.info("Request {} accepted by {}", requestId, targetUsername);

        // Send notification to requester
        notificationService.notifyRequestAccepted(request.getRequester(), request);
        
        return saved;
    }

    public ShoutoutRequest markAsPosted(Long requestId, String username) {
        ShoutoutRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        if (!request.getStatus().equals(ShoutoutRequest.RequestStatus.ACCEPTED)) {
            throw new IllegalStateException("Request must be ACCEPTED to mark as posted");
        }

        if (request.getRequester().getUsername().equals(username)) {
            request.setRequesterPosted(true);
            request.setRequesterPostedAt(LocalDateTime.now());
        } else if (request.getTarget().getUsername().equals(username)) {
            request.setTargetPosted(true);
            request.setTargetPostedAt(LocalDateTime.now());
        } else {
            throw new UnauthorizedException("User not associated with this request");
        }

        // Check if both have posted
        if (request.getRequesterPosted() && request.getTargetPosted()) {
            completeRequest(request);
        }

        ShoutoutRequest saved = requestRepository.save(request);
        log.info("Request {} marked as posted by {}", requestId, username);
        return saved;
    }

    private void completeRequest(ShoutoutRequest request) {
        request.setStatus(ShoutoutRequest.RequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        
        // Add to circle
        addToCircle(request.getRequester(), request.getTarget());
        addToCircle(request.getTarget(), request.getRequester());
        
        log.info("Request {} completed", request.getId());
    }

    private void addToCircle(User user, User friend) {
        // Implementation to add to user circle
        // This would typically update a many-to-many relationship table
    }

    public Page<ShoutoutRequestDto> getPendingRequests(String username, Pageable pageable) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Page<ShoutoutRequest> requests = requestRepository.findByTargetAndStatus(
            user, ShoutoutRequest.RequestStatus.PENDING, pageable);
        
        return requests.map(this::convertToDto);
    }

    public Page<ShoutoutRequestDto> getSentRequests(String username, Pageable pageable) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Page<ShoutoutRequest> requests = requestRepository.findByRequesterAndStatus(
            user, ShoutoutRequest.RequestStatus.ACCEPTED, pageable);
        
        return requests.map(this::convertToDto);
    }

    @Scheduled(fixedDelay = 3600000) // Every hour
    public void checkExpiredRequests() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<ShoutoutRequest> expiredRequests = requestRepository.findExpiredRequests(cutoffTime);

        for (ShoutoutRequest request : expiredRequests) {
            request.setIsExpired(true);
            request.setStatus(ShoutoutRequest.RequestStatus.FAILED);
            requestRepository.save(request);
            
            // Send expiration notification
            notificationService.notifyRequestExpired(request);
            log.info("Request {} marked as expired", request.getId());
        }
    }

    private ShoutoutRequestDto convertToDto(ShoutoutRequest request) {
        LocalDateTime expiryTime = request.getAcceptedAt().plusHours(24);
        long hoursRemaining = Duration.between(LocalDateTime.now(), expiryTime).toHours();
        
        return ShoutoutRequestDto.builder()
            .id(request.getId())
            .requesterUsername(request.getRequester().getUsername())
            .targetUsername(request.getTarget().getUsername())
            .postLink(request.getPostLink())
            .status(request.getStatus().toString())
            .createdAt(request.getCreatedAt())
            .acceptedAt(request.getAcceptedAt())
            .completedAt(request.getCompletedAt())
            .requesterPosted(request.getRequesterPosted())
            .targetPosted(request.getTargetPosted())
            .isExpired(request.getIsExpired())
            .hoursRemaining(Math.max(0, hoursRemaining))
            .build();
    }
}
