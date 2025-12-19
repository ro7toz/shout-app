package com.shoutx.dtos;

import com.shoutx.models.MediaItem;
import java.util.List;

public class ProfileDTO {
    private Long id;
    private String username;
    private String profilePicture;
    private Long followers;
    private Double rating;
    private Integer strikes;
    private String planType;
    private List<MediaItem> mediaItems;

    public ProfileDTO(Long id, String username, String profilePicture, Long followers,
                     Double rating, Integer strikes, String planType, List<MediaItem> mediaItems) {
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
        this.followers = followers;
        this.rating = rating;
        this.strikes = strikes;
        this.planType = planType;
        this.mediaItems = mediaItems;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getProfilePicture() { return profilePicture; }
    public Long getFollowers() { return followers; }
    public Double getRating() { return rating; }
    public Integer getStrikes() { return strikes; }
    public String getPlanType() { return planType; }
    public List<MediaItem> getMediaItems() { return mediaItems; }
}
