package com.shoutx.controller;

import com.shoutx.model.User;
import com.shoutx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

@Controller
@RequestMapping("/legal")
@RequiredArgsConstructor
public class LegalController {

    private final UserService userService;

    @GetMapping("/terms")
    public String termsAndConditions(Authentication authentication, Model model) {
        setCommonAttributes(authentication, model);
        // Content will be in template
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacyPolicy(Authentication authentication, Model model) {
        setCommonAttributes(authentication, model);
        // Content will be in template
        return "privacy";
    }

    @GetMapping("/refund")
    public String refundPolicy(Authentication authentication, Model model) {
        setCommonAttributes(authentication, model);
        // Content will be in template
        return "refund";
    }

    private void setCommonAttributes(Authentication authentication, Model model) {
        User user = null;
        boolean isLoggedIn = false;

        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByUsername(authentication.getName());
            if (userOpt.isPresent()) {
                user = userOpt.get();
                isLoggedIn = true;
            }
        }

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("user", user);
        if (isLoggedIn) {
            model.addAttribute("planType", user.getPlanType());
        }
        
        model.addAttribute("showHeader", true);
        model.addAttribute("showFooter", true);
        model.addAttribute("companyName", "ShoutX");
        model.addAttribute("address", "Poonam Colony, Kota (Rajasthan)");
        model.addAttribute("phone", "+91 9509103148");
        model.addAttribute("email", "tushkinit@gmail.com");
    }
}
