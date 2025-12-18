package com.shout.repository;

import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Filter users by follower count range
     * Used for homepage discovery
     */
    @Query("SELECT u FROM User u WHERE u.followerCount BETWEEN :minFollowers AND :maxFollowers AND u.isActive = true")
    List<User> findByFollowerCountRange(@Param("minFollowers") Integer minFollowers, 
                                        @Param("maxFollowers") Integer maxFollowers);

    /**
     * Filter users by category and follower count range
     * Used for refined discovery
     */
    @Query("SELECT u FROM User u WHERE u.category = :category AND u.followerCount BETWEEN :minFollowers AND :maxFollowers AND u.isActive = true")
    List<User> findByCategoryAndFollowerCountRange(@Param("category") String category,
                                                   @Param("minFollowers") Integer minFollowers,
                                                   @Param("maxFollowers") Integer maxFollowers);

    /**
     * Check if user is banned from social login
     * Used during OAuth authentication
     */
    @Query("SELECT CASE WHEN u.socialLoginBanned = true THEN 1 ELSE 0 END FROM User u WHERE u.username = :username")
    boolean isSocialLoginBanned(@Param("username") String username);

    /**
     * Get users with strike count > 0
     * Used for compliance monitoring
     */
    @Query("SELECT u FROM User u WHERE u.strikeCount > 0 ORDER BY u.strikeCount DESC")
    List<User> findUsersWithStrikes();

    /**
     * Get banned users
     * Used for compliance reporting
     */
    @Query("SELECT u FROM User u WHERE u.accountBanned = true")
    List<User> findBannedUsers();

    /**
     * Find users by category (for discovery)
     */
    List<User> findByCategory(String category);
}
