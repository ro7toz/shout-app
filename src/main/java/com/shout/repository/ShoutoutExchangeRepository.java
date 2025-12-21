package com.shout.repository;

import com.shout.model.ShoutoutExchange;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShoutoutExchangeRepository extends JpaRepository<ShoutoutExchange, Long> {
    List<ShoutoutExchange> findByRequester(User requester);
    List<ShoutoutExchange> findByAcceptor(User acceptor);
    List<ShoutoutExchange> findByStatus(ShoutoutExchange.ExchangeStatus status);
    List<ShoutoutExchange> findByExpiresAtBefore(LocalDateTime date);
    List<ShoutoutExchange> findByStatusAndExpiresAtBefore(ShoutoutExchange.ExchangeStatus status, LocalDateTime date);
    
    // ✅ ADDED: Missing methods
    List<ShoutoutExchange> findByStatusIn(List<ShoutoutExchange.ExchangeStatus> statuses);
    
    @Query("SELECT e FROM ShoutoutExchange e WHERE e.expiresAt < :date AND e.status != :status")
    List<ShoutoutExchange> findByExpiresAtBeforeAndStatusNot(
        @Param("date") LocalDateTime date, 
        @Param("status") ShoutoutExchange.ExchangeStatus status
    );
    
    @Query("SELECT e FROM ShoutoutExchange e WHERE e.expiresAt BETWEEN :start AND :end AND e.status != :status")
    List<ShoutoutExchange> findByExpiresAtBetweenAndStatusNot(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("status") ShoutoutExchange.ExchangeStatus status
    );
    
    @Query("SELECT e FROM ShoutoutExchange e WHERE (e.requester = :user OR e.acceptor = :user) AND e.status = 'PENDING'")
    List<ShoutoutExchange> findPendingExchangesForUser(User user);
    
    @Query("SELECT COUNT(e) FROM ShoutoutExchange e WHERE e.requester = :user AND e.status = 'COMPLETED'")
    Integer countCompletedExchangesAsRequester(User user);
    
    @Query("SELECT COUNT(e) FROM ShoutoutExchange e WHERE e.acceptor = :user AND e.status = 'COMPLETED'")
    Integer countCompletedExchangesAsAcceptor(User user);
}
