package com.shout.repository;

import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByInstagramId(String instagramId);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<User> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    Page<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
        String username, String fullName, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.averageRating DESC")
    Page<User> findTopRatedUsers(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.category = :category AND u.isActive = true ORDER BY u.averageRating DESC")
    List<User> findTopRatedByCategory(@Param("category") String category);
}
