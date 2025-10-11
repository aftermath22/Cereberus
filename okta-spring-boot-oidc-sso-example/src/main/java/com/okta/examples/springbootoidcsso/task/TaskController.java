package com.okta.examples.springbootoidcsso.task;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @GetMapping
    public List<Task> getTasks(Principal principal) {
        // Use the static getTasks method from your repository
        return TaskRepository.getTasks(principal.getName());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Everyone')")
    public void addTask(@RequestBody Task task, Principal principal) {
        // Use the static addTask method
        TaskRepository.addTask(principal.getName(), task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Everyone')")
    public void editTask(@PathVariable String id, @RequestBody Task task, Principal principal) {
        // First, verify the task belongs to the authenticated user
        Task existingTask = TaskRepository.getTask(principal.getName(), id);
        if (existingTask == null) {
            throw new IllegalArgumentException("Task not found or you do not have permission to edit it.");
        }

        // Update the task properties using the correct methods
        existingTask.setDescription(task.getDescription());
        existingTask.setDate(task.getDate());

        // Use the static updateTask method
        TaskRepository.updateTask(principal.getName(), existingTask);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Everyone')")
    public void deleteTask(@PathVariable String id, Principal principal) {
        // First, verify the task belongs to the authenticated user before deleting
        Task existingTask = TaskRepository.getTask(principal.getName(), id);
        if (existingTask == null) {
            throw new IllegalArgumentException("Task not found or you do not have permission to delete it.");
        }

        // Use the static deleteTask method
        TaskRepository.deleteTask(principal.getName(), id);
    }
}