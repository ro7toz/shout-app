package com.shoutx.repositories;

import com.shoutx.models.Exchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    @Query("SELECT e FROM Exchange e WHERE e.user1Id = ?1 OR e.user2Id = ?1 ORDER BY e.createdAt DESC")
    List<Exchange> findByUserId(Long userId, Pageable pageable);
    
    Page<Exchange> findByUser1IdOrUser2Id(Long user1Id, Long user2Id, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM Exchange e WHERE e.user1Id = ?1 OR e.user2Id = ?1")
    Long countByUserId(Long userId);
}
