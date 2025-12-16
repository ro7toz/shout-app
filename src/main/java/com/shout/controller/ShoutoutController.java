package com.shout.controller;

import com.shout.service.ShoutoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shoutout")
@RequiredArgsConstructor
@Slf4j
public class ShoutoutController {

    private final ShoutoutService shoutoutService;

    @PostMapping("/request")
    public String createShoutoutRequest(
            @RequestParam String targetUsername,
            @RequestParam String postLink,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirectAttributes) {

        try {
            String currentUsername = (String) principal.getAttributes().get("login");
            if (currentUsername == null) {
                currentUsername = principal.getName();
            }
            shoutoutService.createShoutoutRequest(currentUsername, targetUsername, postLink);
            redirectAttributes.addFlashAttribute("success", "Shoutout request sent successfully!");
            log.info("Shoutout request sent from {} to {}", currentUsername, targetUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send request: " + e.getMessage());
            log.error("Error creating shoutout request", e);
        }

        return "redirect:/";
    }

    @PostMapping("/accept/{requestId}")
    public String acceptRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirectAttributes) {

        try {
            String currentUsername = (String) principal.getAttributes().get("login");
            if (currentUsername == null) {
                currentUsername = principal.getName();
            }
            shoutoutService.acceptShoutoutRequest(requestId, currentUsername);
            redirectAttributes.addFlashAttribute("success", "Request accepted! You have 24 hours to post.");
            log.info("Request {} accepted by {}", requestId, currentUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            log.error("Error accepting request", e);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/posted/{requestId}")
    public String markAsPosted(
            @PathVariable Long requestId,
            @RequestParam(defaultValue = "true") boolean isRequester,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirectAttributes) {

        try {
            String currentUsername = (String) principal.getAttributes().get("login");
            if (currentUsername == null) {
                currentUsername = principal.getName();
            }
            shoutoutService.markAsPosted(requestId, currentUsername, isRequester);
            redirectAttributes.addFlashAttribute("success", "Great! Your post has been marked.");
            log.info("Request {} marked as posted by {}", requestId, currentUsername);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            log.error("Error marking as posted", e);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/cancel/{requestId}")
    public String cancelRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirectAttributes) {

        try {
            String currentUsername = (String) principal.getAttributes().get("login");
            if (currentUsername == null) {
                currentUsername = principal.getName();
            }
            shoutoutService.cancelShoutoutRequest(requestId, currentUsername);
            redirectAttributes.addFlashAttribute("success", "Request cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }
}