package com.shout.service;

import com.shout.model.*;
import com.shout.repository.UserRatingRepository;
import com.shout.repository.ShoutoutExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Rating Service - Handles user ratings after exchanges
 * CRITICAL: Updates user average ratings and sends notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
   
    private final UserRatingRepository ratingRepository;
    private final ShoutoutExchangeRepository exchangeRepository;
    private final NotificationService notificationService;
   
    /**
     * Rate a user after completing an exchange
     */
    @Transactional
    public UserRating rateUser(
            User rater,
            User ratee,
            ShoutoutExchange exchange,
            Integer rating,
            String review,
            UserRating.RatingCategory category) {
       
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
       
        // Check if exchange is completed
        if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.COMPLETED) {
            throw new RuntimeException("Can only rate completed exchanges");
        }
       
        // Check if already rated
        boolean alreadyRated = ratingRepository.existsByRaterAndExchange(rater, exchange);
        if (alreadyRated) {
            throw new RuntimeException("You have already rated this exchange");
        }
       
        // Create rating
        UserRating userRating = UserRating.builder()
            .rater(rater)
            .ratee(ratee)
            .exchange(exchange)
            .rating(rating)
            .review(review)
            .category(category)
            .build();
       
        UserRating saved = ratingRepository.save(userRating);
       
        // Update user's average rating
        updateUserAverageRating(ratee);
       
        // Send notification
        String message = rater.getName() + " rated you " + rating + " stars";
        notificationService.createNotification(ratee, "New Rating", message, null);
       
        log.info("User {} rated user {} with {} stars for exchange {}",
            rater.getUsername(), ratee.getUsername(), rating, exchange.getId());
       
        return saved;
    }
   
    /**
     * Get all ratings for a user
     */
    public List<UserRating> getUserRatings(User user) {
        return ratingRepository.findByRatee(user);
    }
   
    /**
     * Get user's average rating
     */
    public Double getUserAverageRating(User user) {
        Double avg = ratingRepository.getAverageRating(user);
        return avg != null ? avg : 0.0;
    }
   
    /**
     * Get total rating count for user
     */
    public Integer getUserRatingCount(User user) {
        Integer count = ratingRepository.getTotalRatingCount(user);
        return count != null ? count : 0;
    }
   
    /**
     * Get average rating by category
     */
    public Double getCategoryAverageRating(User user, UserRating.RatingCategory category) {
        Double avg = ratingRepository.getAverageRatingByCategory(user, category);
        return avg != null ? avg : 0.0;
    }
   
    /**
     * Update user's average rating (called after new rating)
     */
    @Transactional
    public void updateUserAverageRating(User user) {
        Double avgRating = ratingRepository.getAverageRating(user);
        Integer totalRatings = ratingRepository.getTotalRatingCount(user);
       
        user.setRating(avgRating != null ? avgRating : 0.0);
        user.setTotalRatings(totalRatings != null ? totalRatings : 0);
       
        log.info("Updated rating for user {}: {} stars from {} ratings",
            user.getUsername(), user.getRating(), user.getTotalRatings());
    }
   
    /**
     * Check if user can rate this exchange
     */
    public boolean canRateExchange(User user, ShoutoutExchange exchange) {
        // Must be part of the exchange
        boolean isParticipant = exchange.getRequester().getId().equals(user.getId()) ||
                               exchange.getAcceptor().getId().equals(user.getId());
       
        if (!isParticipant) {
            return false;
        }
       
        // Exchange must be completed
        if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.COMPLETED) {
            return false;
        }
       
        // Must not have already rated
        boolean alreadyRated = ratingRepository.existsByRaterAndExchange(user, exchange);
        return !alreadyRated;
    }
}