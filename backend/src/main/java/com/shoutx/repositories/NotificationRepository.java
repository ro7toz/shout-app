package com.shoutx.repositories;

import com.shoutx.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size);
    
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    Long countByUserIdAndIsReadFalse(Long userId);
}
