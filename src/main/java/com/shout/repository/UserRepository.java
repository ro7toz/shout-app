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
    
    // ===== INSTAGRAM QUERIES =====
    Optional<User> findByInstagramId(String instagramId);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<User> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    Page<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
        String username, String fullName, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.averageRating DESC")
    Page<User> findTopRatedUsers(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.category = :category AND u.isActive = true ORDER BY u.averageRating DESC")
    List<User> findTopRatedByCategory(@Param("category") String category);
    
    // ===== FACEBOOK AUTHENTICATION QUERIES =====
    /**
     * Find user by Facebook ID
     * Used during login to check if user already exists
     */
    Optional<User> findByFacebookId(String facebookId);
    
    /**
     * Find user by Facebook access token
     * Used to refresh sessions
     */
    Optional<User> findByFacebookAccessToken(String facebookAccessToken);
    
    /**
     * Find user by email
     * Used as fallback if Facebook ID exists but email is unique identifier
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if Facebook ID already exists
     * Used before creating new user
     */
    boolean existsByFacebookId(String facebookId);
    
    /**
     * Find all users who logged in via Facebook
     * Used for analytics/reporting
     */
    @Query("SELECT u FROM User u WHERE u.facebookId IS NOT NULL AND u.isActive = true")
    List<User> findAllFacebookUsers();
}
