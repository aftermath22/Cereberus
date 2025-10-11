package com.okta.examples.springbootoidcsso.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private static final Map<String, List<Task>> userTasks = new ConcurrentHashMap<>();

    public static void addTask(String userId, Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
        }
        userTasks.computeIfAbsent(userId, k -> new ArrayList<>()).add(task);
    }

    public static List<Task> getTasks(String userId) {
        return new ArrayList<>(userTasks.getOrDefault(userId, Collections.emptyList()));
    }

    public static Task getTask(String userId, String taskId) {
        return userTasks.getOrDefault(userId, Collections.emptyList())
                .stream().filter(t -> t.getId().equals(taskId)).findFirst().orElse(null);
    }

    public static void updateTask(String userId, Task updatedTask) {
        List<Task> tasks = userTasks.get(userId);
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(updatedTask.getId())) {
                    tasks.set(i, updatedTask);
                    break;
                }
            }
        }
    }

    public static void deleteTask(String userId, String taskId) {
        List<Task> tasks = userTasks.get(userId);
        if (tasks != null) {
            tasks.removeIf(t -> t.getId().equals(taskId));
        }
    }
}
