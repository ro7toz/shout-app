package com.shout.controller;

import com.shout.service.ShoutoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rating")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final ShoutoutService shoutoutService;

    @GetMapping("/form/{targetUsername}")
    public String showRatingForm(
            @PathVariable String targetUsername,
            Model model) {
        model.addAttribute("targetUsername", targetUsername);
        return "rating-form";
    }

    @PostMapping("/submit")
    public String submitRating(
            @RequestParam String targetUsername,
            @RequestParam Integer score,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirectAttributes) {

        try {
            String currentUsername = (String) principal.getAttributes().get("login");
            if (currentUsername == null) {
                currentUsername = principal.getName();
            }
            shoutoutService.submitRating(currentUsername, targetUsername, score, reason, comment);
            redirectAttributes.addFlashAttribute("success", "Rating submitted successfully!");
            log.info("Rating submitted: {} rated {} with score {}", currentUsername, targetUsername, score);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            log.error("Error submitting rating", e);
        }

        return "redirect:/dashboard";
    }
}