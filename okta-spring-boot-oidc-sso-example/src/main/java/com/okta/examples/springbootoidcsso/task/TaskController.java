package com.okta.examples.springbootoidcsso.task;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @PostMapping
    public Task addTask(@AuthenticationPrincipal OidcUser user, @RequestBody Task task) {
        String userId = user.getSubject();
        TaskRepository.addTask(userId, task);
        return task;
    }

    @GetMapping
    public List<Task> getTasks(@AuthenticationPrincipal OidcUser user) {
    String userId = user.getSubject();
    List<Task> tasks = TaskRepository.getTasks(userId);
    System.out.println("[DEBUG] Tasks for user " + userId + ": " + tasks);
    return tasks;
    }

    @GetMapping("/{id}")
    public Task getTask(@AuthenticationPrincipal OidcUser user, @PathVariable String id) {
        String userId = user.getSubject();
        return TaskRepository.getTask(userId, id);
    }

    @PutMapping("/{id}")
    public Task updateTask(@AuthenticationPrincipal OidcUser user, @PathVariable String id, @RequestBody Task task) {
        String userId = user.getSubject();
        task.setId(id);
        TaskRepository.updateTask(userId, task);
        return task;
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@AuthenticationPrincipal OidcUser user, @PathVariable String id) {
        String userId = user.getSubject();
        TaskRepository.deleteTask(userId, id);
    }
}
