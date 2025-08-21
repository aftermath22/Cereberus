package com.okta.examples.springbootoidcsso.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    // Public welcome page
    @GetMapping("/")
    public String welcome() {
        return "welcome"; // maps to welcome.html
    }

    // Protected profile page
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("user", oidcUser);
        return "profile"; // maps to profile.html
    }
}
