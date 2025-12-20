package com.shout.service;

import com.shout.dto.ExchangeDTO;
import com.shout.model.*;
import com.shout.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shoutout Service - handles shoutout exchanges
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShoutoutService {
   
    private final ShoutoutRequestRepository requestRepository;
    private final UserRepository userRepository;
   
    /**
     * Get user exchanges
     */
    public List<ExchangeDTO> getUserExchanges(Long userId) {
        // Implementation to fetch user exchanges
        List<ExchangeDTO> exchanges = new ArrayList<>();
        // TODO: Implement exchange fetching logic
        return exchanges;
    }
   
    /**
     * Get pending shoutout requests for user
     */
    public List<ShoutoutRequest> getPendingRequests(Long userId, int page, int pageSize) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
       
        // TODO: Implement pagination
        return new ArrayList<>();
    }
   
    /**
     * Get sent shoutout requests
     */
    public List<ShoutoutRequest> getSentRequests(Long userId, int page, int pageSize) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
       
        // TODO: Implement pagination
        return new ArrayList<>();
    }
   
    /**
     * Send shoutout request
     */
    @Transactional
    public ShoutoutRequest sendShoutoutRequest(ShoutoutRequest request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }
   
    /**
     * Get shoutout request by ID
     */
    public Optional<ShoutoutRequest> getShoutoutRequestById(Long id) {
        return requestRepository.findById(id);
    }
   
    /**
     * Update shoutout request
     */
    @Transactional
    public ShoutoutRequest updateShoutoutRequest(ShoutoutRequest request) {
        return requestRepository.save(request);
    }
}