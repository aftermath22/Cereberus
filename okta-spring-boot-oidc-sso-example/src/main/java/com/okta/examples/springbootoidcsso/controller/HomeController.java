package com.okta.examples.springbootoidcsso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HomeController {

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/ia")
    public String tasksRedirect() {
        return "redirect:/tasks";
    }
}
