package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomePageDTO {
    private boolean isLoggedIn;
    private UserProfileDTO currentUser;
    private String message;
    
    public HomePageDTO(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
