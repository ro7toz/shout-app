package com.shout.repository;

import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository - Extended for Facebook login and advanced queries
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   
    Optional<User> findByEmail(String email);
   
    Optional<User> findByInstagramUsername(String instagramUsername);
   
    Optional<User> findByFacebookId(String facebookId);
   
    Optional<User> findByFacebookAccessToken(String accessToken);
   
    boolean existsByEmail(String email);
   
    boolean existsByInstagramUsername(String instagramUsername);
   
    @Query("SELECT u FROM User u WHERE u.followers BETWEEN :minFollowers AND :maxFollowers AND u.isActive = true")
    List<User> findByFollowerCountRange(Integer minFollowers, Integer maxFollowers);
   
    @Query("SELECT u FROM User u WHERE u.category = :category AND u.followers BETWEEN :minFollowers AND :maxFollowers AND u.isActive = true")
    List<User> findByCategoryAndFollowerCountRange(String category, Integer minFollowers, Integer maxFollowers);
   
    @Query("SELECT CASE WHEN u.socialLoginBanned = true THEN 1 ELSE 0 END FROM User u WHERE u.id = :userId")
    boolean isSocialLoginBanned(Long userId);
   
    @Query("SELECT u FROM User u WHERE u.strikeCount > 0 ORDER BY u.strikeCount DESC")
    List<User> findUsersWithStrikes();
   
    @Query("SELECT u FROM User u WHERE u.accountBanned = true")
    List<User> findBannedUsers();
   
    List<User> findByCategory(String category);
}