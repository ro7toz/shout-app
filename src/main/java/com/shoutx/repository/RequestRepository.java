package com.shoutx.repository;

import com.shoutx.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    
    List<Request> findBySenderId(Long senderId);
    
    List<Request> findByReceiverId(Long receiverId);
    
    List<Request> findBySenderIdAndStatus(Long senderId, Request.RequestStatus status);
    
    List<Request> findByReceiverIdAndStatus(Long receiverId, Request.RequestStatus status);
    
    @Query("SELECT r FROM Request r WHERE r.receiver.id = :userId AND r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<Request> getPendingRequestsForUser(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING' AND r.deadline < :now")
    List<Request> getExpiredRequests(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(r) FROM Request r WHERE r.sender.id = :userId AND r.status = 'COMPLETED'")
    Long countCompletedRequestsBySender(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Request r WHERE r.receiver.id = :userId AND r.status = 'COMPLETED'")
    Long countCompletedRequestsByReceiver(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(AVG(r.ratingBySender), 0) FROM Request r WHERE r.receiver.id = :userId")
    Double getAverageRatingForUser(@Param("userId") Long userId);
    
    List<Request> findByPhotoId(Long photoId);
}
