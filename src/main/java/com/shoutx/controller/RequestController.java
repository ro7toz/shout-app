package com.shoutx.controller;

import com.shoutx.dto.RequestDTO;
import com.shoutx.dto.CreateRequestDTO;
import com.shoutx.dto.RateRequestDTO;
import com.shoutx.entity.Request;
import com.shoutx.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@CrossOrigin
public class RequestController {
    
    private final RequestService requestService;
    
    @PostMapping
    public ResponseEntity<RequestDTO> createRequest(@RequestBody CreateRequestDTO request,
                                                     @RequestHeader("Authorization") String token) {
        // Token validation would be done by interceptor/filter
        Long userId = 1L; // Extract from token
        
        Request createdRequest = requestService.createRequest(userId, request.getReceiverId(), request.getPhotoId());
        return ResponseEntity.ok(convertToDTO(createdRequest));
    }
    
    @GetMapping("/received")
    public ResponseEntity<List<RequestDTO>> getReceivedRequests(@RequestHeader("Authorization") String token) {
        Long userId = 1L; // Extract from token
        List<Request> requests = requestService.getPendingRequestsForUser(userId);
        
        return ResponseEntity.ok(requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/sent")
    public ResponseEntity<List<RequestDTO>> getSentRequests(@RequestHeader("Authorization") String token) {
        Long userId = 1L; // Extract from token
        List<Request> requests = requestService.getSentRequests(userId);
        
        return ResponseEntity.ok(requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }
    
    @PutMapping("/{requestId}/accept")
    public ResponseEntity<RequestDTO> acceptRequest(@PathVariable Long requestId,
                                                      @RequestHeader("Authorization") String token) {
        Long userId = 1L; // Extract from token
        Request request = requestService.acceptRequest(requestId, userId);
        return ResponseEntity.ok(convertToDTO(request));
    }
    
    @PutMapping("/{requestId}/complete")
    public ResponseEntity<RequestDTO> completeRequest(@PathVariable Long requestId,
                                                        @RequestHeader("Authorization") String token) {
        Long userId = 1L; // Extract from token
        Request request = requestService.completeRequest(requestId, userId);
        return ResponseEntity.ok(convertToDTO(request));
    }
    
    @PostMapping("/{requestId}/rate")
    public ResponseEntity<?> rateRequest(@PathVariable Long requestId,
                                          @RequestBody RateRequestDTO ratingRequest,
                                          @RequestHeader("Authorization") String token) {
        Long userId = 1L; // Extract from token
        requestService.rateExchange(requestId, userId, ratingRequest.getRating(), ratingRequest.getComment());
        
        return ResponseEntity.ok(new Object() {
            public final boolean success = true;
            public final String message = "Request rated successfully";
        });
    }
    
    private RequestDTO convertToDTO(Request request) {
        return RequestDTO.builder()
                .id(request.getId())
                .senderId(request.getSender().getId())
                .senderName(request.getSender().getName())
                .receiverId(request.getReceiver().getId())
                .receiverName(request.getReceiver().getName())
                .photoUrl(request.getPhoto().getPhotoUrl())
                .status(request.getStatus().toString())
                .deadline(request.getDeadline())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
