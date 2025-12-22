package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private UserInfoResponse user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private String userId;
        private String username;
        private String email;
        private String profilePicture;
        private Long followerCount;
        private String bio;
        private String instagramId;
        private String planType;
        private Integer strikeCount;
        private Boolean isVerified;
    }
}
