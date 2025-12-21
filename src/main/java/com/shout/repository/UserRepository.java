package com.shout.repository;

import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   
    // Email lookup
    Optional<User> findByEmail(String email);
   
    // Instagram lookup
    Optional<User> findByInstagramUsername(String instagramUsername);
    Optional<User> findByInstagramId(String instagramId);
   
    // Facebook lookup (for future use)
    Optional<User> findByFacebookId(String facebookId);
    Optional<User> findByFacebookAccessToken(String accessToken);
   
    // Username lookup
    Optional<User> findByUsername(String username);
   
    // Existence checks
    boolean existsByEmail(String email);
    boolean existsByInstagramUsername(String instagramUsername);
    boolean existsByUsername(String username);
   
    // Follower range queries
    @Query("SELECT u FROM User u WHERE u.followers BETWEEN :minFollowers AND :maxFollowers " +
           "AND u.isActive = true ORDER BY u.followers DESC")
    List<User> findByFollowerCountRange(@Param("minFollowers") Integer minFollowers, 
                                        @Param("maxFollowers") Integer maxFollowers);
   
    @Query("SELECT u FROM User u WHERE u.category = :category " +
           "AND u.followers BETWEEN :minFollowers AND :maxFollowers " +
           "AND u.isActive = true ORDER BY u.followers DESC")
    List<User> findByCategoryAndFollowerCountRange(@Param("category") String category,
                                                    @Param("minFollowers") Integer minFollowers,
                                                    @Param("maxFollowers") Integer maxFollowers);
   
    // Category search
    List<User> findByCategory(String category);
    
    @Query("SELECT u FROM User u WHERE u.category = :category AND u.isActive = true")
    List<User> findActiveByCategoryCategory(@Param("category") String category);
   
    // Search by name or username
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND u.isActive = true")
    List<User> searchByNameOrUsername(@Param("query") String query);
   
    // Social login ban check
    @Query("SELECT CASE WHEN u.socialLoginBanned = true THEN true ELSE false END " +
           "FROM User u WHERE u.id = :userId")
    boolean isSocialLoginBanned(@Param("userId") Long userId);
   
    // Strike and ban queries
    @Query("SELECT u FROM User u WHERE u.strikeCount > 0 ORDER BY u.strikeCount DESC")
    List<User> findUsersWithStrikes();
   
    @Query("SELECT u FROM User u WHERE u.accountBanned = true")
    List<User> findBannedUsers();
   
    // Active users
    List<User> findByIsActive(Boolean isActive);
    
    // Plan type queries
    @Query("SELECT u FROM User u WHERE u.planType = :planType AND u.isActive = true")
    List<User> findByPlanType(@Param("planType") String planType);
    
    // Count queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.planType = 'PRO' AND u.isActive = true")
    Long countProUsers();
}
