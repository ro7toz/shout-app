package com.shout.repository;

import com.shout.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Exchange Repository
 */
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
   
    List<Exchange> findBySenderIdOrRecipientId(Long senderId, Long recipientId);
   
    List<Exchange> findByStatus(String status);
   
    @Query("SELECT e FROM Exchange e WHERE (e.sender.id = :userId OR e.recipient.id = :userId) AND e.status = :status")
    List<Exchange> findUserExchangesByStatus(Long userId, String status);
   
    List<Exchange> findByDeadlineBefore(LocalDateTime deadline);
}