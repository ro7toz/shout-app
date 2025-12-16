package com.shout.repository;

import com.shout.model.ShoutoutRequest;
import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoutoutRequestRepository extends JpaRepository<ShoutoutRequest, Long> {
    Page<ShoutoutRequest> findByTargetAndStatus(User target, ShoutoutRequest.RequestStatus status, Pageable pageable);
    
    Page<ShoutoutRequest> findByRequesterAndStatus(User requester, ShoutoutRequest.RequestStatus status, Pageable pageable);
    
    @Query("SELECT sr FROM ShoutoutRequest sr WHERE sr.status = 'ACCEPTED' AND sr.acceptedAt < :cutoffTime AND sr.isExpired = false")
    List<ShoutoutRequest> findExpiredRequests(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    Long countByTargetAndStatus(User target, ShoutoutRequest.RequestStatus status);
    
    Long countByRequesterAndStatus(User requester, ShoutoutRequest.RequestStatus status);
    
    Optional<ShoutoutRequest> findByIdAndTargetUsername(Long id, String username);
}
