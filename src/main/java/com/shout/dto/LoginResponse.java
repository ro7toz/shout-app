package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO - Returns JWT token and user profile after successful login/signup
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn; // Token expiration time in milliseconds
    private UserProfileDTO user;
}
