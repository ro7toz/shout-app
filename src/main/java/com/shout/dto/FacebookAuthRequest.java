package com.shout.dto;

/**
 * DTO for Facebook authentication request from frontend
 * Contains Facebook access token and user information
 */
public class FacebookAuthRequest {

    private String accessToken;      // Facebook access token from FB.login()
    private String userId;            // Facebook user ID
    private Long expiresIn;           // Token expiration time in seconds
    private String signedRequest;     // Signed request from Facebook
    private String fbName;            // User's Facebook name
    private String fbEmail;           // User's Facebook email
    private String fbProfilePicture;  // User's Facebook profile picture URL

    // Default constructor
    public FacebookAuthRequest() {
    }

    // Constructor with all fields
    public FacebookAuthRequest(String accessToken, String userId, Long expiresIn,
                               String signedRequest, String fbName, String fbEmail,
                               String fbProfilePicture) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.expiresIn = expiresIn;
        this.signedRequest = signedRequest;
        this.fbName = fbName;
        this.fbEmail = fbEmail;
        this.fbProfilePicture = fbProfilePicture;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getSignedRequest() {
        return signedRequest;
    }

    public void setSignedRequest(String signedRequest) {
        this.signedRequest = signedRequest;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbEmail() {
        return fbEmail;
    }

    public void setFbEmail(String fbEmail) {
        this.fbEmail = fbEmail;
    }

    public String getFbProfilePicture() {
        return fbProfilePicture;
    }

    public void setFbProfilePicture(String fbProfilePicture) {
        this.fbProfilePicture = fbProfilePicture;
    }

    @Override
    public String toString() {
        return "FacebookAuthRequest{" +
                "accessToken='" + (accessToken != null ? accessToken.substring(0, 10) + "..." : null) + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresIn=" + expiresIn +
                ", fbName='" + fbName + '\'' +
                ", fbEmail='" + fbEmail + '\'' +
                '}';
    }
}
