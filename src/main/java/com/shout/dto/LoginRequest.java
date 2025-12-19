package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest DTO - Used for login and signup requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String instagramUsername;
    private String instagramToken;
    private Integer followers;
    private String profilePicture;
    private String accountType; // Creator, Business, Personal
    private String email;
}
