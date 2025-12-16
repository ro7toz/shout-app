package com.shout.controller;

import com.shout.model.User;
import com.shout.service.ShoutoutService;
import com.shout.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ShoutoutService shoutoutService;
    private final UserSyncService userSyncService;
    private static final int PAGE_SIZE = 9;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal != null) {
            User user = userSyncService.syncOrCreateUser(principal);
            model.addAttribute("currentUser", user.getUsername());
            model.addAttribute("currentUserObj", user);
        }

        Page<User> users = shoutoutService.getAllUsers(PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("users", users);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);

        return "home";
    }

    @GetMapping("/users/page/{page}")
    public String loadMoreUsers(
            @PathVariable int page,
            Model model) {

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Page<User> users = shoutoutService.getAllUsers(PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());

        return "fragments/cards :: card-list";
    }

    @GetMapping("/category/{category}")
    public String filterByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal != null) {
            model.addAttribute("currentUser", principal.getAttribute("login"));
        }

        Page<User> users = shoutoutService.getUsersByCategory(category, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("users", users);
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);

        return "home";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal != null) {
            model.addAttribute("currentUser", principal.getAttribute("login"));
        }

        Page<User> users = shoutoutService.searchUsers(query, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);

        return "home";
    }
}