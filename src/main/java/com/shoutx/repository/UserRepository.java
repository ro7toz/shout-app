package com.shoutx.repository;

import com.shoutx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByInstagramId(String instagramId);
    
    Optional<User> findByUsername(String username);
    
    List<User> findByPlanType(User.PlanType planType);
    
    List<User> findByIsActiveTrueAndIsBannedFalse();
    
    List<User> findByIsActiveTrueAndIsBannedTrue();
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isBanned = false " +
           "AND (:genre IS NULL OR u.accountType = :genre) " +
           "ORDER BY u.id")
    List<User> searchUsers(@Param("genre") User.AccountType genre);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true AND u.isBanned = false")
    Long countActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.isBanned = false AND u.isActive = true ORDER BY u.rating DESC LIMIT :limit")
    List<User> getTopRatedUsers(@Param("limit") int limit);
}
