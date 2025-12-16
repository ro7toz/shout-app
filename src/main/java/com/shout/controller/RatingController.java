package com.shout.controller;

import com.shout.dto.RatingDto;
import com.shout.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {
        try {
            String raterUsername = authentication.getName();
            String ratedUsername = (String) request.get("ratedUsername");
            Integer score = (Integer) request.get("score");
            String reason = (String) request.get("reason");
            String comment = (String) request.get("comment");
            
            var rating = ratingService.submitRating(raterUsername, ratedUsername, score, reason, comment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<RatingDto>> getUserRatings(@PathVariable String username) {
        var ratings = ratingService.getUserRatings(username);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/average/{username}")
    public ResponseEntity<Double> getUserAverageRating(@PathVariable String username) {
        var avgRating = ratingService.getUserAverageRating(username);
        return ResponseEntity.ok(avgRating);
    }
}
