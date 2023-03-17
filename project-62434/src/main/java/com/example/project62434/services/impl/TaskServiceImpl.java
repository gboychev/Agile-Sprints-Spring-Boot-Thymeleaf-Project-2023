package com.example.project62434.services.impl;

import com.example.project62434.enums.TaskKind;
import com.example.project62434.enums.TaskStatus;
import com.example.project62434.exceptions.EntityNotFoundException;
import com.example.project62434.models.*;
import com.example.project62434.models.Task;
import com.example.project62434.respositories.TaskRepository;
import com.example.project62434.services.SprintService;
import com.example.project62434.services.TaskResultService;
import com.example.project62434.services.TaskService;
import com.example.project62434.services.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    //    @Autowired
    private TaskRepository taskRepository;

    //    @Autowired
    private UserService userService;

    //    @Autowired
    private SprintService sprintService;

    private TaskResultService taskResultService;
    private EntityManager entityManager;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserService userService, SprintService sprintService, @Lazy TaskResultService taskResultService, EntityManager entityManager) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.sprintService = sprintService;
        this.taskResultService = taskResultService;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Task getTaskById(Long taskId) {
//        Session session = entityManager.unwrap(Session.class);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task", "id", taskId.toString()));
//        session.refresh(task);
        Task task2 = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task", "id", taskId.toString()));
        task2 = task;
        return task;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        List<Task> taskList = taskRepository.findAll();
        for (Task t : taskList) {
            t.getDevelopersAssigned().size();
            if (t.getSprint() != null) {
                t.getSprint().toString();
            }
        }
        return taskList;
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskById(Long taskId, Task task) {
        Task taskToUpdate = getTaskById(taskId);
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setKind(task.getKind());
        taskToUpdate.setSprint(task.getSprint());
        taskToUpdate.setDevelopersAssigned(task.getDevelopersAssigned());
        taskToUpdate.setEstimatedEffort(task.getEstimatedEffort());
        taskToUpdate.setModified(LocalDateTime.now());
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long taskId) {
        Task task = getTaskById(taskId);
        userService.getUsers().stream().forEach(u -> u.getAssignedTasksForDeveloper().remove(task));
        sprintService.getAllSprints().stream().forEach(s -> s.getTasks().remove(task));
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public Task createTask(String status, String title, String kind, String description, String addedBy, String developers, String sprint, String estimatedEffort, String tags, String username) {
        Task task = new Task();
        TaskStatus statusEnum;
        switch (status) {
            case "ACTIVE" -> statusEnum = TaskStatus.ACTIVE;
            case "PLANNED" -> statusEnum = TaskStatus.PLANNED;
            case "COMPLETED" -> statusEnum = TaskStatus.COMPLETED;
            default -> statusEnum = TaskStatus.PLANNED;
        }
        task.setStatus(statusEnum);
        task.setTitle(title);
        TaskKind kindEnum;
        switch (kind) {
            case "RESEARCH" -> kindEnum = TaskKind.RESEARCH;
            case "DESIGN" -> kindEnum = TaskKind.DESIGN;
            case "PROTOTYPING" -> kindEnum = TaskKind.PROTOTYPING;
            case "IMPLEMENTATION" -> kindEnum = TaskKind.IMPLEMENTATION;
            case "QA" -> kindEnum = TaskKind.QA;
            case "OPERATIONS" -> kindEnum = TaskKind.OPERATIONS;
            case "DOCUMENTATION" -> kindEnum = TaskKind.DOCUMENTATION;
            default -> kindEnum = TaskKind.OTHER;
        }
        task.setKind(kindEnum);
        task.setDescription(description);
        if (addedBy != null && !addedBy.isEmpty()) {
            task.setAddedBy(userService.getUserByUsername(addedBy));
        } else {
            task.setAddedBy(userService.getUserByUsername(username));
        }
        if (sprint != null && !sprint.isEmpty()) {
            task.setSprint(sprintService.getSprintById(Long.parseLong(sprint)));
        }
        if (estimatedEffort != null && !sprint.isEmpty()) {
            Integer integer = Integer.parseInt(estimatedEffort);
            task.setEstimatedEffort(integer);
        }
        task.setTags(tags);
        taskRepository.save(task);
        if (developers != null && !developers.isEmpty()) {
            updateTaskDevelopers(task, developers);
        }
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskStatusById(Long id, String num) {
        Task task = getTaskById(id);
        num = num.trim();
        TaskStatus taskStatus;
        switch (num) {
            case "1" -> taskStatus = TaskStatus.PLANNED;
            case "2" -> taskStatus = TaskStatus.ACTIVE;
            case "3" -> taskStatus = TaskStatus.COMPLETED;
            default ->
                    throw new IllegalArgumentException("You can only enter values of 1, 2 or 3, when editing task status");
        }
        Task currentTask = getTaskById(id);
        currentTask.setStatus(taskStatus);
        if (taskStatus == TaskStatus.COMPLETED) {
            List<User> devsToCurrentTask = currentTask.getDevelopersAssigned();
            for (User u : devsToCurrentTask) {
                u.getAssignedTasksForDeveloper().remove(currentTask);
            }
//            currentTask.getDevelopersAssigned().forEach(d -> d.getAssignedTasksForDeveloper().remove(currentTask)); //remove the task from the developers active task list
            List<TaskResult> taskResultsForThisParticularTask =
                    taskResultService.getAllTaskResults().stream()
                            .filter(tr -> Objects.equals(tr.getTask().getId(), currentTask.getId())).toList(); //get all task results that are associated with this task
            currentTask.getDevelopersAssigned().
                    forEach(d -> d.getCompletedTaskResultsForDeveloper().addAll(taskResultsForThisParticularTask)); //fill the developers task result list with the task results associated with this task
        }
//        if (taskStatus == TaskStatus.COMPLETED) {
//            userService.
//        }
        return taskRepository.save(currentTask);
    }

    @Transactional
    public Task updateTaskDevelopers(Task task, String developers) {
        List<User> developersList = new ArrayList<>();
        String[] developerArr = developers.split(",");
        for (String s : developerArr) {
            s = s.trim();
            User dev = userService.getDeveloperByUsername(s);
            userService.updateAssignedTasks(dev.getId(), task);
            developersList.add(dev);
        }
        task.setDevelopersAssigned(developersList);
        return task;
    }


}
//
//    @Override
//    public Task getTaskById(Long taskId) {
//        return taskRepository.findById(taskId).orElseThrow( () -> new EntityNotFoundException("Task", "id", taskId.toString()));
//    }
//    @Override
//    public List<Task> getAllTasks() {
//        return taskRepository.findAll();
//    }
//
//    @Override
//    @Transactional
//    public Task createTask(Task task) {
//        if (taskRepository.existsByTaskname(task.getTaskname())) {
//            throw new InvalidEntityDataException(String.format("Task with taskname [%s] already exists!",
//                    task.getTaskname()));
//        }
//        return taskRepository.save(task);
//    }
//
//    @Override
//    @Transactional
//    public Task updateTaskById(Long taskId, Task task) {
//        Task task = getTaskById(taskId);
//
//        return taskRepository.save(task);
//    }
//
//    @Override
//    @Transactional
//    public void deleteTaskById(Long taskId) {
//        Task foundTask = getTaskById(taskId);
//        taskRepository.delete(foundTask);
//    }

