package com.shoutx.repository;

import com.shoutx.entity.Strike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrikeRepository extends JpaRepository<Strike, Long> {
    
    List<Strike> findByUserId(Long userId);
    
    @Query("SELECT COUNT(s) FROM Strike s WHERE s.user.id = :userId")
    Integer getStrikeCountForUser(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Strike s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<Strike> getUserStrikesOrderByNewest(@Param("userId") Long userId);
}
