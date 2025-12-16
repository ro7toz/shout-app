package com.shout.service;

import com.shout.exception.ResourceNotFoundException;
import com.shout.model.ShoutoutRequest;
import com.shout.model.User;
import com.shout.repository.ShoutoutRequestRepository;
import com.shout.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShoutoutServiceTest {
    @Mock
    private ShoutoutRequestRepository requestRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private ShoutoutService shoutoutService;
    
    private User requester;
    private User target;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        requester = new User();
        requester.setUsername("user1");
        
        target = new User();
        target.setUsername("user2");
    }
    
    @Test
    public void testCreateRequest() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(requester));
        when(userRepository.findById("user2")).thenReturn(Optional.of(target));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        ShoutoutRequest result = shoutoutService.createRequest("user1", "user2", "https://instagram.com/p/abc");
        
        assertNotNull(result);
        assertEquals(ShoutoutRequest.RequestStatus.PENDING, result.getStatus());
        verify(notificationService, times(1)).notifyRequestReceived(any(), any(), any());
    }
    
    @Test
    public void testCreateRequestUserNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            shoutoutService.createRequest("user1", "user2", "https://instagram.com/p/abc");
        });
    }
}
