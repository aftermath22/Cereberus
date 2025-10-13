package com.okta.examples.springbootoidcsso.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HomeController {

    @Value("${task-manager.base-url}")
    private String taskManagerUrl;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/ib")
    public ModelAndView home(@AuthenticationPrincipal OidcUser user) throws JsonProcessingException {
        ModelAndView mav = new ModelAndView();
        if (user != null) {
            mav.addObject("user", user.getUserInfo());
            mav.addObject("idToken", user.getIdToken().getTokenValue());
            mav.addObject(
                "claims",
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user.getClaims())
            );
        } else {
            mav.addObject("user", null);
            mav.addObject("idToken", null);
            mav.addObject("claims", "{} (not authenticated)");
        }
        mav.addObject("taskManagerUrl", taskManagerUrl);
        mav.setViewName("home");
        return mav;
    }
}