package com.example.project62434.services;

import com.example.project62434.models.TaskResult;

import java.util.List;

public interface TaskResultService {
    TaskResult getTaskResultById(Long taskResultId);

    List<TaskResult> getAllTaskResults();

    TaskResult createTaskResult(TaskResult taskResult);

    TaskResult updateTaskResultById(Long taskResultId, TaskResult taskResult);

    void deleteTaskResultById(Long taskResultId);

    TaskResult createTaskResult(String username, String task, String actualEffort, String verifiedBy, String description);
}
