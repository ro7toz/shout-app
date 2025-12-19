package com.shoutx.pages;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/pages/profile")
public class ProfilePageController {
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId, Authentication auth) {
        return ResponseEntity.ok(new Object() {
            public Long getId() { return userId; }
            public String getUsername() { return "user" + userId; }
            public Long getFollowers() { return 10000L; }
            public Double getRating() { return 4.5; }
            public Integer getStrikes() { return 0; }
        });
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody Object data) {
        return ResponseEntity.ok("Profile updated");
    }
    @PostMapping("/{userId}/media")
    public ResponseEntity<?> uploadMedia(@PathVariable Long userId, @RequestBody Object media) {
        return ResponseEntity.ok("Media uploaded");
    }
}