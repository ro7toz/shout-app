package com.shoutx.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchanges")
public class Exchange {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long user1Id;
    
    @Column(nullable = false)
    private String user1Name;
    
    @Column(nullable = false)
    private Long user2Id;
    
    @Column(nullable = false)
    private String user2Name;
    
    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, COMPLETED, EXPIRED
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = true)
    private LocalDateTime completedAt;
    
    @Column(nullable = true)
    private Double rating;
    
    @Column(nullable = false)
    private LocalDateTime deadline = LocalDateTime.now().plusDays(1);
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }
    
    public String getUser1Name() { return user1Name; }
    public void setUser1Name(String user1Name) { this.user1Name = user1Name; }
    
    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }
    
    public String getUser2Name() { return user2Name; }
    public void setUser2Name(String user2Name) { this.user2Name = user2Name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}
