package com.shout.controller;

import com.shout.model.*;
import com.shout.repository.UserRepository;
import com.shout.service.RatingService;
import com.shout.service.ShoutoutExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final RatingService ratingService;
    private final ShoutoutExchangeService exchangeService;
    private final UserRepository userRepository;

    /**
     * Rate a user after exchange
     */
    @PostMapping("/rate")
    public ResponseEntity<?> rateUser(@RequestAttribute("user") User rater,
                                     @RequestParam Long exchangeId,
                                     @RequestParam Integer rating,
                                     @RequestParam(required = false) String review,
                                     @RequestParam String category) {
        try {
            ShoutoutExchange exchange = exchangeService.getExchangeById(exchangeId);
            
            if (exchange.getStatus() != ShoutoutExchange.ExchangeStatus.COMPLETED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Exchange not completed"));
            }

            // Determine who to rate
            User ratee;
            if (exchange.getRequester().getUsername().equals(rater.getUsername())) {
                ratee = exchange.getAcceptor();
            } else if (exchange.getAcceptor().getUsername().equals(rater.getUsername())) {
                ratee = exchange.getRequester();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Not part of this exchange"));
            }

            UserRating.RatingCategory ratingCategory = UserRating.RatingCategory.valueOf(category.toUpperCase());
            UserRating userRating = ratingService.rateUser(rater, ratee, exchange, rating, review, ratingCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(userRating);
        } catch (Exception e) {
            log.error("Error rating user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user's ratings
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserRatings(@PathVariable String username) {
        try {
            User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            List<UserRating> ratings = ratingService.getUserRatings(user);
            Double averageRating = ratingService.getUserAverageRating(user);
            Integer ratingCount = ratingService.getUserRatingCount(user);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("averageRating", averageRating);
            response.put("totalRatings", ratingCount);
            response.put("ratings", ratings);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user ratings", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found"));
        }
    }

    /**
     * Get category-specific average rating
     */
    @GetMapping("/user/{username}/category/{category}")
    public ResponseEntity<?> getCategoryRating(@PathVariable String username,
                                              @PathVariable String category) {
        try {
            User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            UserRating.RatingCategory ratingCategory = UserRating.RatingCategory.valueOf(category.toUpperCase());
            Double avgRating = ratingService.getCategoryAverageRating(user, ratingCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("category", category);
            response.put("averageRating", avgRating);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching category rating", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    private static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
