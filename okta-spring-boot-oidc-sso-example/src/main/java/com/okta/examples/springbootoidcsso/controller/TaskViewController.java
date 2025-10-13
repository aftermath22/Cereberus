package com.okta.examples.springbootoidcsso.controller;

import com.okta.examples.springbootoidcsso.task.Task;
import com.okta.examples.springbootoidcsso.task.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ia") // Set the base mapping to /ia
public class TaskViewController {

    @Value("${learnhub.base-url}")
    private String learnhubUrl;

    @GetMapping
    public String listTasks(@AuthenticationPrincipal OidcUser user, Model model) {
        String userId = user.getSubject();
        model.addAttribute("user", user); // Add the full user object for the greeting
        model.addAttribute("tasks", TaskRepository.getTasks(userId));
        model.addAttribute("learnhubUrl", learnhubUrl);
        return "tasks"; // This will now be our main page
    }

    @PostMapping("/add")
    public String addTask(@AuthenticationPrincipal OidcUser user, @RequestParam String description, @RequestParam String date) {
        String userId = user.getSubject();
        TaskRepository.addTask(userId, new Task(null, description, date));
        return "redirect:/ia"; // Redirect back to the main task list
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@AuthenticationPrincipal OidcUser user, @PathVariable String id, Model model) {
        String userId = user.getSubject();
        Task task = TaskRepository.getTask(userId, id);
        model.addAttribute("task", task);
        model.addAttribute("learnhubUrl", learnhubUrl);
        return "edit-task";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@AuthenticationPrincipal OidcUser user, @PathVariable String id, @RequestParam String description, @RequestParam String date) {
        String userId = user.getSubject();
        TaskRepository.updateTask(userId, new Task(id, description, date));
        return "redirect:/ia"; // Redirect back to the main task list
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@AuthenticationPrincipal OidcUser user, @PathVariable String id) {
        String userId = user.getSubject();
        TaskRepository.deleteTask(userId, id);
        return "redirect:/ia"; // Redirect back to the main task list
    }
}