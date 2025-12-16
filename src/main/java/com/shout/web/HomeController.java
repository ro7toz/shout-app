package com.shout.web;

import com.shout.config.FacebookConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private FacebookConfig facebookConfig;

    @GetMapping("/")
    public String home(Model model) {
        // Pass Facebook App ID to the template
        model.addAttribute("facebookAppId", facebookConfig.getAppId());
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
