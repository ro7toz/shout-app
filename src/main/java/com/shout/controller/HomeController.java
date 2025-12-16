package com.shout.controller;

import com.shout.dto.UserProfileDto;
import com.shout.model.User;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final UserRepository userRepository;

    @GetMapping
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users/page/{page}")
    @ResponseBody
    public Page<UserProfileDto> getUsers(
            @PathVariable int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<User> users = userRepository.findByIsActive(true, pageable);
        return users.map(this::convertToDto);
    }

    @GetMapping("/users/search")
    @ResponseBody
    public Page<UserProfileDto> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<User> users = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            query, query, pageable);
        return users.map(this::convertToDto);
    }

    @GetMapping("/category/{category}")
    @ResponseBody
    public Page<UserProfileDto> getUsersByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<User> users = userRepository.findByCategoryIgnoreCase(category, pageable);
        return users.map(this::convertToDto);
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(
            @PathVariable String username,
            Model model) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", convertToDto(user));
        return "profile";
    }

    private UserProfileDto convertToDto(User user) {
        return UserProfileDto.builder()
            .username(user.getUsername())
            .fullName(user.getFullName())
            .profilePicUrl(user.getProfilePicUrl())
            .category(user.getCategory())
            .followerCount(user.getFollowerCount())
            .biography(user.getBiography())
            .websiteUrl(user.getWebsiteUrl())
            .averageRating(user.getAverageRating())
            .totalRatings(user.getTotalRatings().longValue())
            .build();
    }
}
