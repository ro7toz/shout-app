package com.shout.repository;

import com.shout.model.Rating;
import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRatedUser(User ratedUser);

    Page<Rating> findByRatedUser(User ratedUser, Pageable pageable);

    Long countByRatedUserAndScoreLessThan(User user, Integer score);
}