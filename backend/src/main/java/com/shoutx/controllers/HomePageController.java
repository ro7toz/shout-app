package com.shoutx.controllers;

import com.shoutx.models.User;
import com.shoutx.models.ShoutoutRequest;
import com.shoutx.services.UserService;
import com.shoutx.services.ShoutoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pages/home")
public class HomePageController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ShoutoutService shoutoutService;

    @GetMapping("/featured-creators")
    public ResponseEntity<?> getFeaturedCreators() {
        List<User> featuredCreators = userService.getFeaturedCreators(10);
        return ResponseEntity.ok(featuredCreators);
    }

    @GetMapping("/trending-requests")
    public ResponseEntity<?> getTrendingRequests() {
        List<ShoutoutRequest> trendingRequests = shoutoutService.getTrendingRequests(10);
        return ResponseEntity.ok(trendingRequests);
    }

    @GetMapping("/platform-stats")
    public ResponseEntity<?> getPlatformStats() {
        long totalUsers = userService.getTotalUsers();
        long totalExchanges = shoutoutService.getTotalExchanges();
        long totalReach = shoutoutService.getTotalReach();
        
        return ResponseEntity.ok(new Object() {
            public long getTotalUsers() { return totalUsers; }
            public long getTotalExchanges() { return totalExchanges; }
            public long getTotalReach() { return totalReach; }
        });
    }
}
