package com.shoutx.dto;

import com.shoutx.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private String name;
    private String token;
    private User.PlanType planType;
    private String message;
    private Boolean isNewUser;
}
