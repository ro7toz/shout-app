package com.shoutx.dto;

import com.shoutx.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String name;
    private String username;
    private String profilePictureUrl;
    private String bio;
    private User.AccountType accountType;
    private Boolean isVerified;
    private Double rating;
    private List<String> photos;
}
