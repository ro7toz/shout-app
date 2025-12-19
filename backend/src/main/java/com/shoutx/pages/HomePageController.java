package com.shoutx.pages;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api/pages/home")
public class HomePageController {
    @GetMapping("/featured-creators")
    public ResponseEntity<?> getFeaturedCreators() {
        List<String> creators = Arrays.asList("creator1", "creator2", "creator3");
        return ResponseEntity.ok(creators);
    }
    @GetMapping("/trending-requests")
    public ResponseEntity<?> getTrendingRequests() {
        return ResponseEntity.ok(Arrays.asList("request1", "request2"));
    }
    @GetMapping("/platform-stats")
    public ResponseEntity<?> getPlatformStats() {
        return ResponseEntity.ok(new Object() {
            public long getTotalUsers() { return 50000L; }
            public long getTotalExchanges() { return 100000L; }
            public long getTotalReach() { return 5000000L; }
        });
    }
}