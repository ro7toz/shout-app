package com.shoutx.repository;

import com.shoutx.model.ShoutoutExchange;
import com.shoutx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoutoutExchangeRepository extends JpaRepository<ShoutoutExchange, Long> {

    List<ShoutoutExchange> findBySender(User sender);
    List<ShoutoutExchange> findByReceiver(User receiver);
    List<ShoutoutExchange> findBySenderAndReceiver(User sender, User receiver);

    @Query("SELECT e FROM ShoutoutExchange e WHERE e.sender = :user OR e.receiver = :user ORDER BY e.createdAt DESC")
    List<ShoutoutExchange> findAllExchangesForUser(User user);

    @Query("SELECT e FROM ShoutoutExchange e WHERE e.receiver = :user AND e.status = 'PENDING' ORDER BY e.createdAt DESC")
    List<ShoutoutExchange> findPendingRequestsForUser(User user);

    @Query("SELECT e FROM ShoutoutExchange e WHERE e.status = 'COMPLETED' AND (e.sender = :user OR e.receiver = :user) ORDER BY e.createdAt DESC")
    List<ShoutoutExchange> findCompletedExchanges(User user);

    @Query("SELECT e FROM ShoutoutExchange e WHERE e.expiresAt < :now AND e.completionStatus = 'INCOMPLETE'")
    List<ShoutoutExchange> findExpiredIncompleteExchanges(LocalDateTime now);

    @Query("SELECT COUNT(e) FROM ShoutoutExchange e WHERE e.sender = :user AND e.completionStatus = 'COMPLETE'")
    long countSuccessfulExchanges(User user);

    @Query("SELECT COALESCE(SUM(e.reachCount), 0) FROM ShoutoutExchange e WHERE e.sender = :user OR e.receiver = :user")
    long getTotalReach(User user);
}
