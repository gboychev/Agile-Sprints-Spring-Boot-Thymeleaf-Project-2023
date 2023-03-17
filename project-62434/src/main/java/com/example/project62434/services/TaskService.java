package com.example.project62434.services;

import com.example.project62434.models.Task;

import java.util.List;

public interface TaskService {
    Task getTaskById(Long taskId);

    List<Task> getAllTasks();

    Task createTask(Task task);

    Task updateTaskById(Long taskId, Task task);

    void deleteTaskById(Long taskId);

    Task createTask(String status, String title, String kind, String description, String addedBy, String developers, String sprint, String estimatedEffort, String tags, String username);

    Task updateTaskStatusById(Long id, String num);
}
