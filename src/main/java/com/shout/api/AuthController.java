package com.shout.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shout.service.FacebookAuthService;
import com.shout.dto.FacebookAuthRequest;
import com.shout.dto.AuthResponse;

/**
 * Controller for handling Facebook authentication
 * Processes login requests from frontend and syncs user data
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private FacebookAuthService facebookAuthService;

    /**
     * Endpoint for Facebook login
     * Called from frontend after user successfully logs in with Facebook
     * 
     * Expected request body:
     * {
     *   "accessToken": "facebook_access_token",
     *   "userId": "facebook_user_id",
     *   "expiresIn": 5184000,
     *   "signedRequest": "signed_request_string",
     *   "fbName": "User Name",
     *   "fbEmail": "user@example.com",
     *   "fbProfilePicture": "https://..."
     * }
     * 
     * @param request Facebook authentication request from frontend
     * @return Authentication response with JWT token and user info
     */
    @PostMapping("/facebook")
    public ResponseEntity<AuthResponse> facebookLogin(@RequestBody FacebookAuthRequest request) {
        try {
            // Validate the Facebook access token with Facebook servers
            boolean isValid = facebookAuthService.validateAccessToken(request.getAccessToken());
            
            if (!isValid) {
                return ResponseEntity.badRequest().body(
                    new AuthResponse(
                        false,
                        "Invalid Facebook access token",
                        null,
                        null
                    )
                );
            }

            // Sync or create user in database
            AuthResponse response = facebookAuthService.syncOrCreateUser(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(
                    false,
                    "Authentication failed: " + e.getMessage(),
                    null,
                    null
                )
            );
        }
    }

    /**
     * Endpoint to check if access token is still valid
     * Called periodically to refresh session
     * 
     * @param accessToken Facebook access token
     * @return Validity status
     */
    @GetMapping("/facebook/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestParam String accessToken) {
        try {
            boolean isValid = facebookAuthService.validateAccessToken(accessToken);
            
            return ResponseEntity.ok(
                new AuthResponse(
                    isValid,
                    isValid ? "Token is valid" : "Token is invalid",
                    null,
                    null
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(
                    false,
                    "Validation failed: " + e.getMessage(),
                    null,
                    null
                )
            );
        }
    }

    /**
     * Endpoint to refresh user session
     * Called to extend user's session validity
     * 
     * @param accessToken Current Facebook access token
     * @return New JWT token if valid
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshSession(@RequestParam String accessToken) {
        try {
            AuthResponse response = facebookAuthService.refreshSession(accessToken);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(
                    false,
                    "Session refresh failed: " + e.getMessage(),
                    null,
                    null
                )
            );
        }
    }
}
