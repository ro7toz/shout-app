package com.shout.service;

import com.shout.dto.RatingDto;
import com.shout.exception.BadRequestException;
import com.shout.exception.ResourceNotFoundException;
import com.shout.model.Rating;
import com.shout.model.User;
import com.shout.repository.RatingRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Rating submitRating(String raterUsername, String ratedUsername, Integer score, String reason, String comment) {
        User rater = userRepository.findById(raterUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", raterUsername));
        
        User ratedUser = userRepository.findById(ratedUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", ratedUsername));

        if (score < 1 || score > 5) {
            throw new BadRequestException("Score must be between 1 and 5");
        }

        if (rater.getUsername().equals(ratedUser.getUsername())) {
            throw new BadRequestException("Cannot rate yourself");
        }

        // Check if already rated
        if (ratingRepository.existsByRaterAndRatedUser(rater, ratedUser)) {
            throw new BadRequestException("You have already rated this user");
        }

        Rating rating = Rating.builder()
            .rater(rater)
            .ratedUser(ratedUser)
            .score(score)
            .reason(reason)
            .comment(comment)
            .build();

        Rating saved = ratingRepository.save(rating);
        
        // Update average rating
        updateUserRating(ratedUser);
        
        // Notify if low rating
        if (score <= 2) {
            notificationService.notifyBadRating(ratedUser, score);
        }
        
        log.info("{} rated {} with score {}", raterUsername, ratedUsername, score);
        return saved;
    }

    private void updateUserRating(User user) {
        Double avgRating = ratingRepository.getAverageRatingForUser(user);
        Long totalRatings = ratingRepository.countRatingsForUser(user);
        
        user.setAverageRating(avgRating != null ? avgRating : 5.0);
        user.setTotalRatings(totalRatings != null ? totalRatings.intValue() : 0);
        
        userRepository.save(user);
    }

    public List<RatingDto> getUserRatings(String username) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Rating> ratings = ratingRepository.findByRatedUser(user);
        return ratings.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Double getUserAverageRating(String username) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Double avgRating = ratingRepository.getAverageRatingForUser(user);
        return avgRating != null ? avgRating : 5.0;
    }

    private RatingDto convertToDto(Rating rating) {
        return RatingDto.builder()
            .id(rating.getId())
            .raterUsername(rating.getRater().getUsername())
            .ratedUserUsername(rating.getRatedUser().getUsername())
            .score(rating.getScore())
            .reason(rating.getReason())
            .comment(rating.getComment())
            .createdAt(rating.getCreatedAt())
            .build();
    }
}
