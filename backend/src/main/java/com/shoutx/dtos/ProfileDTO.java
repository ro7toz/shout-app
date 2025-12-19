package com.shoutx.dtos;

public class ProfileDTO {
    private Long id;
    private String username;
    private String profilePicture;
    private Long followers;
    private Double rating;
    private Integer strikes;
    private String planType;

    public ProfileDTO(Long id, String username, String pic, Long followers, Double rating, Integer strikes, String planType) {
        this.id = id;
        this.username = username;
        this.profilePicture = pic;
        this.followers = followers;
        this.rating = rating;
        this.strikes = strikes;
        this.planType = planType;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getProfilePicture() { return profilePicture; }
    public Long getFollowers() { return followers; }
    public Double getRating() { return rating; }
    public Integer getStrikes() { return strikes; }
    public String getPlanType() { return planType; }
}