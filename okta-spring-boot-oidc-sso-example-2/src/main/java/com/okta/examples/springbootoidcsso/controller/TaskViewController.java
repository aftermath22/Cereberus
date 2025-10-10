package com.okta.examples.springbootoidcsso.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class TaskViewController {
    @GetMapping("/tasks")
    public String userTasks(@AuthenticationPrincipal OidcUser user, Model model) {
        String userId = user.getSubject();
        String apiUrl = "http://localhost:8080/api/tasks";
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> tasks = Arrays.asList();
        try {
            HttpHeaders headers = new HttpHeaders();
            // Use access token from claims if available, else fallback to ID token
            String accessToken = (String) user.getClaims().get("access_token");
            System.out.println("[DEBUG] Access token used for Task Manager: " + accessToken);
            String tokenToUse = (accessToken != null) ? accessToken : user.getIdToken().getTokenValue();
            headers.setBearerAuth(tokenToUse);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map[].class);
            tasks = Arrays.asList(response.getBody());
            System.out.println("[DEBUG] Tasks fetched from Task Manager: " + tasks);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch tasks: " + e.getMessage());
        }
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}
