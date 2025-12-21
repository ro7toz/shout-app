package com.shout.repository;

import com.shout.model.UserRating;
import com.shout.model.User;
import com.shout.model.ShoutoutExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> findByRatee(User ratee);
    List<UserRating> findByRater(User rater);
    
    // ✅ ADDED: Missing method
    boolean existsByRaterAndExchange(User rater, ShoutoutExchange exchange);
    
    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.ratee = :user")
    Double getAverageRating(User user);
    
    @Query("SELECT COUNT(ur) FROM UserRating ur WHERE ur.ratee = :user")
    Integer getTotalRatingCount(User user);
    
    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.ratee = :user AND ur.category = :category")
    Double getAverageRatingByCategory(User user, UserRating.RatingCategory category);
}
