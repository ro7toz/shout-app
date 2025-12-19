package com.shoutx.repository;

import com.shoutx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByInstagramId(String instagramId);

    @Query("SELECT u FROM User u WHERE u.isAccountBanned = false AND u.isAccountDeleted = false ORDER BY u.followerCount DESC")
    List<User> findActiveUsersSortedByFollowers();

    @Query("SELECT u FROM User u WHERE u.isAccountBanned = false AND u.isAccountDeleted = false AND u.followerCount BETWEEN :minFollowers AND :maxFollowers ORDER BY RAND()")
    List<User> findUsersByFollowerRange(Long minFollowers, Long maxFollowers);

    @Query("SELECT u FROM User u WHERE u.planType = 'PRO' AND u.isAccountBanned = false AND u.isAccountDeleted = false")
    List<User> findProUsers();

    long countByIsAccountBannedTrue();
    long countByIsAccountDeletedTrue();
}
