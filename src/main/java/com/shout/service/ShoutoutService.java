package com.shout.service;

import com.shout.model.*;
import com.shout.repository.ShoutoutRequestRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoutoutService {
    private final ShoutoutRequestRepository shoutoutRequestRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Create a new shoutout request with media type validation
     */
    @Transactional
    public ShoutoutRequest createRequest(String requesterUsername, String targetUsername, 
                                        String postLink, ShoutoutRequest.MediaType mediaType) {
        User requester = userRepository.findById(requesterUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester not found"));
        
        User target = userRepository.findById(targetUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target not found"));

        // ✅ NEW: Validate media type access based on subscription
        if (!subscriptionService.canAccessMediaType(requester, mediaType)) {
            log.warn("❌ User {} tried to request {} but not PRO", requesterUsername, mediaType);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Upgrade to PRO to request posts and reels"
            );
        }

        ShoutoutRequest request = ShoutoutRequest.builder()
            .requester(requester)
            .target(target)
            .postLink(postLink)
            .mediaType(mediaType)
            .status(ShoutoutRequest.RequestStatus.PENDING)
            .build();

        ShoutoutRequest saved = shoutoutRequestRepository.save(request);
        log.info("✅ Shoutout request created: {} -> {} ({})", 
            requesterUsername, targetUsername, mediaType);
        
        return saved;
    }

    /**
     * Accept a shoutout request
     */
    @Transactional
    public ShoutoutRequest acceptRequest(Long requestId) {
        ShoutoutRequest request = shoutoutRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        request.setStatus(ShoutoutRequest.RequestStatus.ACCEPTED);
        request.setAcceptedAt(LocalDateTime.now());

        ShoutoutRequest saved = shoutoutRequestRepository.save(request);
        log.info("✅ Request {} accepted by {}", requestId, request.getTarget().getUsername());
        
        return saved;
    }

    /**
     * Get all pending requests for a user
     */
    public List<ShoutoutRequest> getPendingRequests(String targetUsername) {
        return shoutoutRequestRepository.findByTargetUsernameAndStatus(
            targetUsername, 
            ShoutoutRequest.RequestStatus.PENDING
        );
    }

    /**
     * Get user's sent requests
     */
    public List<ShoutoutRequest> getSentRequests(String requesterUsername) {
        return shoutoutRequestRepository.findByRequesterUsername(requesterUsername);
    }
}
