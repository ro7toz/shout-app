package com.shout.controller;

import com.shout.repository.ShoutoutRequestRepository;
import com.shout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;
    private final ShoutoutRequestRepository requestRepository;

    @GetMapping
    public String dashboard(
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {

        String username = (String) principal.getAttributes().get("login");
        if (username == null) {
            username = principal.getName();
        }
        var user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            var pendingRequests = requestRepository.findByTarget(user.get(), PageRequest.of(0, 10));
            var sentRequests = requestRepository.findByRequester(user.get(), PageRequest.of(0, 10));

            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("sentRequests", sentRequests);
            model.addAttribute("user", user.get());
        }

        return "dashboard";
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(
            @PathVariable String username,
            Model model) {

        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            model.addAttribute("profile", user.get());
            return "profile";
        }

        return "redirect:/";
    }
}