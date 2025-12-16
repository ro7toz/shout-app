package com.shout.service;

import com.shout.exception.BadRequestException;
import com.shout.exception.ResourceNotFoundException;
import com.shout.model.Rating;
import com.shout.model.User;
import com.shout.repository.RatingRepository;
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

public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private RatingService ratingService;
    
    private User rater;
    private User ratedUser;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        rater = new User();
        rater.setUsername("rater");
        
        ratedUser = new User();
        ratedUser.setUsername("rated");
    }
    
    @Test
    public void testSubmitValidRating() {
        when(userRepository.findById("rater")).thenReturn(Optional.of(rater));
        when(userRepository.findById("rated")).thenReturn(Optional.of(ratedUser));
        when(ratingRepository.existsByRaterAndRatedUser(rater, ratedUser)).thenReturn(false);
        when(ratingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        Rating result = ratingService.submitRating("rater", "rated", 5, "Great!", "Amazing work");
        
        assertNotNull(result);
        assertEquals(5, result.getScore());
    }
    
    @Test
    public void testSubmitRatingInvalidScore() {
        when(userRepository.findById("rater")).thenReturn(Optional.of(rater));
        when(userRepository.findById("rated")).thenReturn(Optional.of(ratedUser));
        
        assertThrows(BadRequestException.class, () -> {
            ratingService.submitRating("rater", "rated", 6, "Great!", "Amazing work");
        });
    }
    
    @Test
    public void testCannotRateSelf() {
        rater.setUsername("same");
        ratedUser.setUsername("same");
        
        when(userRepository.findById("same")).thenReturn(Optional.of(rater));
        
        assertThrows(BadRequestException.class, () -> {
            ratingService.submitRating("same", "same", 5, "Great!", "Amazing work");
        });
    }
}
