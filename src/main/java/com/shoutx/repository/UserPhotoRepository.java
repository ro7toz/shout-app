package com.shoutx.repository;

import com.shoutx.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    
    List<UserPhoto> findByUserId(Long userId);
    
    long countByUserId(Long userId);
    
    @Query("SELECT up FROM UserPhoto up WHERE up.user.id = :userId ORDER BY up.createdAt DESC")
    List<UserPhoto> getUserPhotosOrderByNewest(@Param("userId") Long userId);
    
    void deleteByUserIdAndId(Long userId, Long photoId);
}
