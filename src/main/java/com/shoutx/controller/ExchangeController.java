package com.shoutx.controller;

import com.shoutx.model.ShoutoutExchange;
import com.shoutx.model.User;
import com.shoutx.model.UserMedia;
import com.shoutx.service.ShoutoutExchangeService;
import com.shoutx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ShoutoutExchangeService exchangeService;
    private final UserService userService;

    /**
     * Create a new shoutout exchange request
     * User A selects media from their profile to send to User B
     */
    @PostMapping("/send")
    public String sendShoutoutRequest(
            @RequestParam Long receiverId,
            @RequestParam Long mediaId,
            Authentication authentication) {
        
        User sender = getUserFromAuthentication(authentication);
        if (sender == null) return "redirect:/login";

        Optional<User> receiverOpt = userService.getUserById(receiverId);
        if (receiverOpt.isEmpty()) return "redirect:/dashboard?error=User not found";

        User receiver = receiverOpt.get();

        // Get the media
        // TODO: Get media from repository
        // UserMedia media = mediaRepository.findById(mediaId).orElseThrow();

        try {
            // For now, create with null media - update repository access
            exchangeService.createExchangeRequest(sender, receiver, null);
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }

        return "redirect:/dashboard?success=Request sent successfully!";
    }

    /**
     * Accept a pending shoutout request
     * User can now select media to repost
     */
    @PostMapping("/{exchangeId}/accept")
    public String acceptRequest(
            @PathVariable Long exchangeId,
            Authentication authentication) {
        
        User receiver = getUserFromAuthentication(authentication);
        if (receiver == null) return "redirect:/login";

        // TODO: Get exchange from repository
        // ShoutoutExchange exchange = exchangeRepository.findById(exchangeId).orElseThrow();

        try {
            // exchangeService.acceptExchangeRequest(receiver, exchange);
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }

        return "redirect:/dashboard";
    }

    /**
     * Complete repost for an exchange
     * User selects media to repost in response
     */
    @PostMapping("/{exchangeId}/repost")
    public String completeRepost(
            @PathVariable Long exchangeId,
            @RequestParam Long mediaId,
            Authentication authentication) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            // TODO: Implement repost completion
            // exchangeService.completeRepost(exchange, user, receiverMedia);
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }

        return "redirect:/exchange/" + exchangeId + "?success=Repost completed! You will be redirected to Instagram.";
    }

    /**
     * View exchange details (popup)
     * Shows completion status, time remaining, rating option if complete
     */
    @GetMapping("/{exchangeId}")
    public String viewExchange(
            @PathVariable Long exchangeId,
            Authentication authentication,
            Model model) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            // TODO: Get exchange from repository
            // ShoutoutExchange exchange = exchangeRepository.findById(exchangeId).orElseThrow();
            // model.addAttribute("exchange", exchange);
            // model.addAttribute("user", user);
        } catch (Exception e) {
            return "redirect:/dashboard?error=Exchange not found";
        }

        return "exchange-popup";
    }

    /**
     * Mark exchange as rated (after completion)
     */
    @PostMapping("/{exchangeId}/rate")
    public String rateExchange(
            @PathVariable Long exchangeId,
            @RequestParam Double rating,
            @RequestParam(required = false) String review,
            Authentication authentication) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            // TODO: Implement rating storage
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }

        return "redirect:/dashboard?success=Exchange rated!";
    }

    /**
     * Reject/Cancel an exchange request
     */
    @PostMapping("/{exchangeId}/reject")
    public String rejectRequest(
            @PathVariable Long exchangeId,
            Authentication authentication) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            // TODO: Implement rejection logic
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }

        return "redirect:/dashboard";
    }

    // Helper methods

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userService.getUserByUsername(authentication.getName()).orElse(null);
    }
}
