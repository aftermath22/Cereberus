package com.okta.examples.springbootoidcsso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${learnhub.base-url}")
    private String learnhubUrl;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/ia")
    public String tasksRedirect(Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("learnhubUrl", learnhubUrl);
        if (user != null) {
            model.addAttribute("user", user.getUserInfo());
        }
        return "home";
    }
}