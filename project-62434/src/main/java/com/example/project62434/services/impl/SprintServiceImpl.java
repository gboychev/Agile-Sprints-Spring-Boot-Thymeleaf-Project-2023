package com.example.project62434.services.impl;

import com.example.project62434.models.*;
import com.example.project62434.respositories.SprintRepository;
import com.example.project62434.services.SprintService;
import com.example.project62434.services.TaskResultService;
import com.example.project62434.services.TaskService;
import com.example.project62434.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
//@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {
//    @Autowired
    private final SprintRepository sprintRepository;

//    @Autowired
    private final UserService userService;

    private final TaskService taskService;

//    @Autowired
    private final TaskResultService taskResultService;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, UserService userService,@Lazy TaskService taskService, TaskResultService taskResultService) {
        this.sprintRepository = sprintRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.taskResultService = taskResultService;
    }

    @Override
    public Sprint getSprintById(Long sprintId) {
        return sprintRepository.findById(sprintId).orElseThrow(() -> new IllegalArgumentException("Could not find Sprint with id " + sprintId + " in the database"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sprint> getAllSprints() {
        List<Sprint> result = sprintRepository.findAll();
        for (Sprint sprint : result) {
            sprint.getCompletedTaskResults().size();
            sprint.getDevelopers().size();
            sprint.getTasks().size();
        }
        return result;
    }

    @Override
    @Transactional
    public Sprint createSprint(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    @Override
    public Sprint updateSprintById(Long sprintId, Sprint sprint) {
        Sprint existingSprint = sprintRepository.findById(sprintId).orElse(null);
        if (existingSprint == null) {
            return null;
        }
        existingSprint.setDevelopers(sprint.getDevelopers());
        existingSprint.setDuration(sprint.getDuration());
        existingSprint.setOwner(sprint.getOwner());
        existingSprint.setModified(LocalDateTime.now());
        existingSprint.setCompletedTaskResults(sprint.getCompletedTaskResults());
        existingSprint.setEndDate(sprint.getEndDate());
        existingSprint.setTasks(sprint.getTasks());
        return sprintRepository.save(existingSprint);
    }

    @Override
    public void deleteSprintById(Long sprintId) {
        sprintRepository.deleteById(sprintId);
    }

    @Override
    public Sprint createSprint(String owner, String developers, String tasks, String completedTaskResults, String duration, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Sprint sprint = new Sprint();
        sprint.setCreated(now);
        sprint.setModified(now);
        if (duration != null && !owner.isEmpty()) {
            int integer = Integer.parseInt(duration);
            sprint.setDuration(integer);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sprint.setStartDate(LocalDate.parse(startDate, formatter).atStartOfDay());
        }
        if (endDate != null && !endDate.isEmpty()) {
            sprint.setEndDate(LocalDate.parse(endDate, formatter).atStartOfDay());
        }
        if (owner != null && !owner.isEmpty()) {
            sprint.setOwner(userService.getOwnerByUserName(owner));
        }
        if (developers != null && !developers.isEmpty()) {
            updateSprintDevelopers(sprint, developers);
        }
        if (tasks != null && !tasks.isEmpty()) {
            updateSprintTasks(sprint, tasks);
        }
        if (completedTaskResults != null && !completedTaskResults.isEmpty()) {
            updateCompletedTaskResults(sprint, completedTaskResults);
        }
        return sprintRepository.save(sprint);
    }

    @Transactional(readOnly = true)
    public Sprint updateCompletedTaskResults(Sprint sprint, String completedTaskResults) {
        List<TaskResult> taskSet = new ArrayList<>();
        String[] tasksArr = completedTaskResults.split(",");
        for (String s : tasksArr) {
            s.trim();
            TaskResult task = taskResultService.getTaskResultById(Long.parseLong(s));
            taskSet.add(task);
        }
        sprint.setCompletedTaskResults(taskSet);
        return sprint;
    }

    @Transactional(readOnly = true)
    public Sprint updateSprintTasks(Sprint sprint, String tasks) {
        Set<Task> taskSet = new HashSet<>();
        String[] tasksArr = tasks.split(",");
        for (String s : tasksArr) {
            s.trim();
            Task task = taskService.getTaskById(Long.parseLong(s));
            taskSet.add(task);
        }
        sprint.setTasks(taskSet);
        return sprint;
    }

    @Transactional(readOnly = true)
    public Sprint updateSprintDevelopers(Sprint sprint, String developers) {
        Set<User> developersSet = new HashSet<>();
        String[] developerArr = developers.split(",");
        for (String s : developerArr) {
            s.trim();
            User dev = userService.getDeveloperByUsername(s);
            developersSet.add(dev);
        }
        sprint.setDevelopers(developersSet);
        return sprint;
    }

}

