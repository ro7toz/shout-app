package com.shout.repository;

import com.shout.model.ComplianceRecord;
import com.shout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByUser(User user);
    List<ComplianceRecord> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT COUNT(cr) FROM ComplianceRecord cr WHERE cr.user = :user")
    Integer getViolationCount(User user);
    
    @Query("SELECT COUNT(cr) FROM ComplianceRecord cr WHERE cr.user = :user AND cr.strikeNumber = 3")
    Integer getFinalStrikeCount(User user);
    
    List<ComplianceRecord> findByAccountBanned(Boolean banned);
    List<ComplianceRecord> findByViolationType(ComplianceRecord.ViolationType type);
}
