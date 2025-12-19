package com.shoutx.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "plans")
public class Plan {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name; // BASIC, PRO
    
    @Column(nullable = false)
    private Integer price = 0; // In INR, 0 for free
    
    @Column(nullable = false)
    private Integer dailyRequests = 10;
    
    @Column(nullable = false)
    private String allowedMediaTypes = "STORY"; // Comma-separated
    
    @Column(nullable = false)
    private String features = ""; // Comma-separated
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    
    @JsonProperty("priceINR")
    public Integer getPriceINR() { return price; }
    
    public Integer getDailyRequests() { return dailyRequests; }
    public void setDailyRequests(Integer dailyRequests) { this.dailyRequests = dailyRequests; }
    
    public String getAllowedMediaTypes() { return allowedMediaTypes; }
    public void setAllowedMediaTypes(String allowedMediaTypes) { this.allowedMediaTypes = allowedMediaTypes; }
    
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
