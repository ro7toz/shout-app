package com.shoutx.pages;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/pages/dashboard")
public class DashboardPageController {
    @GetMapping
    public ResponseEntity<?> getDashboard(Authentication auth) {
        return ResponseEntity.ok(new Object() {
            public Long getTotalReach() { return 50000L; }
            public Long getProfileVisits() { return 5000L; }
            public Integer getRepostsCount() { return 250; }
            public Double getCompletionRate() { return 95.5; }
        });
    }
    @GetMapping("/exchanges")
    public ResponseEntity<?> getExchanges() {
        return ResponseEntity.ok("Recent exchanges");
    }
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok("User statistics");
    }
}