package com.shout.controller;

import com.shout.dto.UserProfileDto;
import com.shout.model.User;
import com.shout.repository.UserRepository;
import com.shout.service.NotificationService;
import com.shout.service.ShoutoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final UserRepository userRepository;
    private final ShoutoutService shoutoutService;
    private final NotificationService notificationService;

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        
        User user = userRepository.findById(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(0, 10);
        
        model.addAttribute("user", convertToDto(user));
        model.addAttribute("pendingRequests", shoutoutService.getPendingRequests(username, pageable));
        model.addAttribute("sentRequests", shoutoutService.getSentRequests(username, pageable));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(username));
        
        return "dashboard";
    }

    private UserProfileDto convertToDto(User user) {
        return UserProfileDto.builder()
            .username(user.getUsername())
            .fullName(user.getFullName())
            .profilePicUrl(user.getProfilePicUrl())
            .category(user.getCategory())
            .followerCount(user.getFollowerCount())
            .biography(user.getBiography())
            .averageRating(user.getAverageRating())
            .totalRatings(user.getTotalRatings().longValue())
            .build();
    }
}
