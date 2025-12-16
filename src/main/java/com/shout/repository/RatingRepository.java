package com.shout.repository;

import com.shout.model.Rating;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedUser(User ratedUser);
    
    boolean existsByRaterAndRatedUser(User rater, User ratedUser);
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.ratedUser = :user")
    Double getAverageRatingForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser = :user")
    Long countRatingsForUser(@Param("user") User user);
}
