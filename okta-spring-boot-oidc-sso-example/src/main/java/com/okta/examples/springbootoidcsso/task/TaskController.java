// package com.okta.examples.springbootoidcsso.task;

// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.security.Principal;
// import java.util.List;

// @RestController
// @RequestMapping("/api/tasks")
// @CrossOrigin(origins = "*")
// public class TaskController {

//     @GetMapping
//     public List<Task> getTasks(Principal principal) {
//         // Use the static getTasks method from your repository
//         return TaskRepository.getTasks(principal.getName());
//     }

//     @PostMapping
//     @PreAuthorize("hasAuthority('Everyone')")
//     public void addTask(@RequestBody Task task, Principal principal) {
//         // Use the static addTask method
//         TaskRepository.addTask(principal.getName(), task);
//     }

//     @PutMapping("/{id}")
//     @PreAuthorize("hasAuthority('Everyone')")
//     public void editTask(@PathVariable String id, @RequestBody Task task, Principal principal) {
//         // First, verify the task belongs to the authenticated user
//         Task existingTask = TaskRepository.getTask(principal.getName(), id);
//         if (existingTask == null) {
//             throw new IllegalArgumentException("Task not found or you do not have permission to edit it.");
//         }

//         // Update the task properties using the correct methods
//         existingTask.setDescription(task.getDescription());
//         existingTask.setDate(task.getDate());

//         // Use the static updateTask method
//         TaskRepository.updateTask(principal.getName(), existingTask);
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasAuthority('Everyone')")
//     public void deleteTask(@PathVariable String id, Principal principal) {
//         // First, verify the task belongs to the authenticated user before deleting
//         Task existingTask = TaskRepository.getTask(principal.getName(), id);
//         if (existingTask == null) {
//             throw new IllegalArgumentException("Task not found or you do not have permission to delete it.");
//         }

//         // Use the static deleteTask method
//         TaskRepository.deleteTask(principal.getName(), id);
//     }
// }

package com.okta.examples.springbootoidcsso.task;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public Collection<Task> getTasks(@AuthenticationPrincipal OidcUser user) {
        return taskRepository.findAll().stream()
            .filter(task -> user.getSubject().equals(task.getUserId()))
            .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task, @AuthenticationPrincipal OidcUser user) {
        task.setUserId(user.getSubject());
        task.setCreated(new Date());
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask, @AuthenticationPrincipal OidcUser user) {
        Task task = taskRepository.findById(id).get();
        task.setText(updatedTask.getText());
        task.setCreated(updatedTask.getCreated());
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id, @AuthenticationPrincipal OidcUser user) {
        taskRepository.deleteById(id);
    }
}