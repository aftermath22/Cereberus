package com.okta.examples.springbootoidcsso.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    private final String oktaSystemLogUrl = "https://integrator-1697993-admin.okta.com/report/system_log_2?search=&fromTime=2025-10-03T00%3A00%3A00Z&toTime=2025-10-10T23%3A59%3A59Z&locale=Asia%2FCalcutta&limit=20&view=list&topLeftLongitude=-174.375&topLeftLatitude=77.23507365492469&bottomRightLongitude=177.18749999999997&bottomRightLatitude=-44.84029065139799&mapZoom=2";

    
    @GetMapping("/ia/admin")
    @PreAuthorize("hasAuthority('Admins')")
    public String adminPage(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            // This is our debugging step
            System.out.println("--- User Claims ---");
            System.out.println(principal.getClaims());
            System.out.println("--------------------");
        }
        // return "redirect:" + oktaSystemLogUrl; // your admin view name
        return "admin"; 
    }

    // Public welcome page
    @GetMapping("/")
    public String welcome() {
        return "welcome"; // maps to welcome.html
    }

    // Protected profile page
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("user", oidcUser);
        System.out.println("--- User Claims ---");
        System.out.println(oidcUser.getClaims());
        System.out.println("--------------------");
        return "profile"; // maps to profile.html
    }
}
