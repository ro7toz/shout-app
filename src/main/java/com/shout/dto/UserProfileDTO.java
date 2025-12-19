package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String profilePicture;
    private String planType; // BASIC or PRO
    private Integer followers;
    private String accountType; // Creator, Business, Personal, Influencer
    private Boolean isVerified;
    private Double rating; // Average rating out of 5
    private Integer strikes; // Number of strikes (0-3, 3 = banned)
    private Integer dailyRequestsSent;
    private Integer dailyRequestsAccepted;
    private List<UserMediaDTO> mediaItems;
    private Boolean isOwnProfile;
}
