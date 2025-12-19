package com.shoutx.controller;

import com.shoutx.model.User;
import com.shoutx.model.UserMedia;
import com.shoutx.service.UserService;
import com.shoutx.service.ShoutoutExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ShoutoutExchangeService exchangeService;

    /**
     * View self profile
     * Logged in users can see their own profile with edit/delete media options
     */
    @GetMapping("/me")
    public String viewMyProfile(Authentication authentication, Model model) {
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        model.addAttribute("isOwnProfile", true);
        model.addAttribute("user", user);
        model.addAttribute("userMedia", userService.getUserMedia(user));
        model.addAttribute("successfulExchanges", exchangeService.getSuccessfulExchangeCount(user));
        model.addAttribute("rating", user.getRating());
        model.addAttribute("strikeCount", user.getStrikeCount());
        model.addAttribute("isVerified", user.getIsInstagramVerified());
        
        setHeaderAndFooter(model, user);
        return "profile";
    }

    /**
     * View other user's profile
     * Cannot delete/edit their media, can only repost
     */
    @GetMapping("/{userId}")
    public String viewUserProfile(
            @PathVariable Long userId,
            Authentication authentication,
            Model model) {
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/404";
        }

        User viewedUser = userOpt.get();

        // Check if account is banned
        if (viewedUser.getIsAccountBanned() || viewedUser.getIsAccountDeleted()) {
            return "redirect:/account-not-found";
        }

        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userService.getUserByUsername(authentication.getName()).orElse(null);
        }

        model.addAttribute("isOwnProfile", currentUser != null && currentUser.getId().equals(viewedUser.getId()));
        model.addAttribute("user", viewedUser);
        model.addAttribute("userMedia", userService.getUserMedia(viewedUser));
        model.addAttribute("successfulExchanges", exchangeService.getSuccessfulExchangeCount(viewedUser));
        model.addAttribute("rating", viewedUser.getRating());
        model.addAttribute("isVerified", viewedUser.getIsInstagramVerified());
        model.addAttribute("currentUser", currentUser);

        if (currentUser != null) {
            setHeaderAndFooter(model, currentUser);
        }

        return "profile-other";
    }

    /**
     * Add media to profile (max 3, min 1)
     * POST endpoint for HTMX or form submission
     */
    @PostMapping("/me/media/add")
    public String addMedia(
            @RequestParam String mediaUrl,
            @RequestParam String mediaType,
            @RequestParam(defaultValue = "false") Boolean isFromInstagram,
            Authentication authentication,
            Model model) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            UserMedia.MediaType type = UserMedia.MediaType.valueOf(mediaType.toUpperCase());
            userService.addMedia(user, mediaUrl, type, isFromInstagram);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }

        return "redirect:/profile/me";
    }

    /**
     * Delete media from profile
     * Cannot delete last photo
     */
    @PostMapping("/me/media/{mediaId}/delete")
    public String deleteMedia(
            @PathVariable Long mediaId,
            Authentication authentication) {
        
        User user = getUserFromAuthentication(authentication);
        if (user == null) return "redirect:/login";

        try {
            userService.deleteMedia(user, mediaId);
        } catch (RuntimeException e) {
            return "redirect:/profile/me?error=" + e.getMessage();
        }

        return "redirect:/profile/me";
    }

    /**
     * Add rating to user (after exchange completion)
     */
    @PostMapping("/{userId}/rate")
    public String rateUser(
            @PathVariable Long userId,
            @RequestParam Double rating,
            @RequestParam String review,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        if (currentUser == null) return "redirect:/login";

        Optional<User> ratedUserOpt = userService.getUserById(userId);
        if (ratedUserOpt.isEmpty()) return "redirect:/404";

        try {
            User ratedUser = ratedUserOpt.get();
            userService.addRating(ratedUser, rating);
        } catch (RuntimeException e) {
            return "redirect:/profile/" + userId + "?error=" + e.getMessage();
        }

        return "redirect:/profile/" + userId;
    }

    /**
     * Report/Flag user for non-compliance
     */
    @PostMapping("/{userId}/report")
    public String reportUser(
            @PathVariable Long userId,
            @RequestParam String reason,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        if (currentUser == null) return "redirect:/login";

        Optional<User> reportedUserOpt = userService.getUserById(userId);
        if (reportedUserOpt.isEmpty()) return "redirect:/404";

        // TODO: Implement report logic - add strike if needed
        // For now, just add a strike
        User reportedUser = reportedUserOpt.get();
        reportedUser.addStrike();
        // userService.save(reportedUser);

        return "redirect:/profile/" + userId + "?reported=true";
    }

    // Helper methods

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userService.getUserByUsername(authentication.getName()).orElse(null);
    }

    private void setHeaderAndFooter(Model model, User user) {
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("currentUser", user);
        model.addAttribute("planType", user.getPlanType());
        model.addAttribute("showHeader", true);
        model.addAttribute("showFooter", true);
        model.addAttribute("companyName", "ShoutX");
        model.addAttribute("address", "Poonam Colony, Kota (Rajasthan)");
        model.addAttribute("phone", "+91 9509103148");
        model.addAttribute("email", "tushkinit@gmail.com");
    }
}
