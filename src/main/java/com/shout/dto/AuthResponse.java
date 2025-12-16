package com.shout.dto;

/**
 * DTO for authentication response sent to frontend
 * Contains success status, message, JWT token, and user info
 */
public class AuthResponse {

    private boolean success;        // Whether authentication was successful
    private String message;         // Response message (error or success message)
    private String token;           // JWT token for authenticated requests
    private UserInfoResponse user;  // User information

    // Default constructor
    public AuthResponse() {
    }

    // Constructor with success and message
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Constructor with all fields
    public AuthResponse(boolean success, String message, String token, UserInfoResponse user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + (token != null ? token.substring(0, 10) + "..." : null) + '\'' +
                ", user=" + user +
                '}';
    }

    /**
     * Inner class for user information response
     */
    public static class UserInfoResponse {
        private String userId;
        private String username;
        private String email;
        private String profilePicture;
        private Long followerCount;
        private String bio;
        private String instagramId;

        // Constructors
        public UserInfoResponse() {
        }

        public UserInfoResponse(String userId, String username, String email,
                               String profilePicture, Long followerCount) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.profilePicture = profilePicture;
            this.followerCount = followerCount;
        }

        // Getters and Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public Long getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Long followerCount) {
            this.followerCount = followerCount;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getInstagramId() {
            return instagramId;
        }

        public void setInstagramId(String instagramId) {
            this.instagramId = instagramId;
        }

        @Override
        public String toString() {
            return "UserInfoResponse{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", followerCount=" + followerCount +
                    '}';
        }
    }
}
