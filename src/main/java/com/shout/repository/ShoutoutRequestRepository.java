package com.shout.repository;

import com.shout.model.ShoutoutRequest;
import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShoutoutRequestRepository extends JpaRepository<ShoutoutRequest, Long> {

    List<ShoutoutRequest> findByStatus(ShoutoutRequest.RequestStatus status);

    @Query("SELECT r FROM ShoutoutRequest r WHERE " +
           "r.status = com.shout.model.ShoutoutRequest.RequestStatus.ACCEPTED AND " +
           "r.acceptedAt IS NOT NULL AND " +
           "r.acceptedAt < :timestamp")
    List<ShoutoutRequest> findExpiredRequests(LocalDateTime timestamp);

    Page<ShoutoutRequest> findByRequester(User requester, Pageable pageable);

    Page<ShoutoutRequest> findByTarget(User target, Pageable pageable);

    @Query("SELECT r FROM ShoutoutRequest r WHERE r.requester = :user OR r.target = :user ORDER BY r.createdAt DESC")
    Page<ShoutoutRequest> findUserRequests(User user, Pageable pageable);

    Long countByRequesterAndStatus(User requester, ShoutoutRequest.RequestStatus status);

    Long countByTargetAndStatus(User target, ShoutoutRequest.RequestStatus status);
}