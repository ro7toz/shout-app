package com.shoutx.repository;

import com.shoutx.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByReportedUserId(Long reportedUserId);
    
    List<Report> findByReporterId(Long reporterId);
    
    @Query("SELECT r FROM Report r WHERE r.status = 'OPEN' ORDER BY r.createdAt ASC")
    List<Report> getOpenReports();
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedUser.id = :userId AND r.status = 'RESOLVED'")
    Long countResolvedReportsForUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedUser.id = :userId")
    Long countTotalReportsForUser(@Param("userId") Long userId);
}
