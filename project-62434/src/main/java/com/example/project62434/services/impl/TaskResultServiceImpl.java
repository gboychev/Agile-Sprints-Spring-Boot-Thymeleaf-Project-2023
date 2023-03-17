package com.example.project62434.services.impl;

import com.example.project62434.exceptions.EntityNotFoundException;
import com.example.project62434.models.TaskResult;
import com.example.project62434.respositories.TaskResultRepository;
import com.example.project62434.respositories.UserRepository;
import com.example.project62434.services.TaskResultService;
import com.example.project62434.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class TaskResultServiceImpl implements TaskResultService {
    private final TaskResultRepository taskResultRepository;

    private final TaskService taskService;
    private final UserRepository userRepository;

    @Autowired
    public TaskResultServiceImpl(TaskResultRepository taskResultRepository, @Lazy TaskService taskService, UserRepository userRepository) {
        this.taskResultRepository = taskResultRepository;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @Override
    public TaskResult getTaskResultById(Long taskResultId) {
        return taskResultRepository.findById(taskResultId).orElseThrow(() -> new EntityNotFoundException("Task Result", "id", taskResultId.toString()));
    }

    @Override
    public List<TaskResult> getAllTaskResults() {
        return taskResultRepository.findAll();
    }

    @Override
    @Transactional
    public TaskResult createTaskResult(TaskResult taskResult) {
        return taskResultRepository.save(taskResult);
    }

    @Override
    @Transactional
    public TaskResult updateTaskResultById(Long taskResultId, TaskResult taskResult) {
        TaskResult foundTaskResult = getTaskResultById(taskResultId);

        foundTaskResult.setTask(taskResult.getTask());
        foundTaskResult.setResultsDescription(taskResult.getResultsDescription());
        foundTaskResult.setTask(taskResult.getTask());
        foundTaskResult.setModified(LocalDateTime.now());
        foundTaskResult.setVerifiedBy(taskResult.getVerifiedBy());

        return taskResultRepository.save(foundTaskResult);
    }

    @Override
    @Transactional
    public void deleteTaskResultById(Long taskResultId) {
        TaskResult foundTaskResult = getTaskResultById(taskResultId);
        taskResultRepository.delete(foundTaskResult);
    }

    @Override
    @Transactional
    public TaskResult createTaskResult(String username, String task, String actualEffort, String verifiedBy, String description) {
        TaskResult result = new TaskResult();
        result.setTask(taskService.getTaskById(Long.parseLong(task)));
        result.setCreated(LocalDateTime.now());
        result.setVerifiedBy(userRepository.getUserByUsername(username).get());
        result.setActualEffort(Integer.parseInt(actualEffort));
        result.setResultsDescription(description);
        result.setModified(LocalDateTime.now());
        return taskResultRepository.save(result);
    }

}
