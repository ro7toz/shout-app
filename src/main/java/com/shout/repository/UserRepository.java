package com.shout.repository;

import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.averageRating DESC, u.followerCount DESC")
    Page<User> findAllActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.category = :category ORDER BY u.averageRating DESC")
    Page<User> findByCategory(String category, Pageable pageable);

    @Query("SELECT DISTINCT u.category FROM User u WHERE u.isActive = true")
    List<String> findAllCategories();

    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchUsers(String query, Pageable pageable);

    Boolean existsByUsername(String username);
}