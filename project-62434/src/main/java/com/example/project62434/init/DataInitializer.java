package com.example.project62434.init;

import com.example.project62434.dto.UserDto;
import com.example.project62434.enums.Role;
import com.example.project62434.enums.TaskKind;
import com.example.project62434.models.Project;
import com.example.project62434.models.Sprint;
import com.example.project62434.models.Task;
import com.example.project62434.models.User;
import com.example.project62434.services.*;
import com.example.project62434.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final SprintService sprintService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskResultService taskResultService;
    private final ProjectResultService projectResultService;
    private final SprintResultService sprintResultService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.DEVELOPER);
        roles.add(Role.PRODUCT_OWNER);
        HashSet<Role> prodOwn = new HashSet<Role>();
        prodOwn.add(Role.PRODUCT_OWNER);
        HashSet<Role> dev = new HashSet<Role>();
        dev.add(Role.DEVELOPER);
        List<User> INIT_USERS = List.of(
                new User("adminLastName", "adminLastName", "adminUsername", ".Parola1", "email@gmail.com", "https://www.pngitem.com/pimgs/m/150-1503945_transparent-user-png-default-user-image-png-png.png", roles),
                new User("admin2Name", "adminLastName2", "adminUsername2", ".Parola1", "emailAdmin2@gmail.com", "https://www.pngitem.com/pimgs/m/150-1503945_transparent-user-png-default-user-image-png-png.png", roles),
                new User("Eugene", "Peshov", "pe6o", ".Parola1", "emailEugene@gmail.com", "https://www.pngitem.com/pimgs/m/150-1503945_transparent-user-png-default-user-image-png-png.png", prodOwn),
                new User("Georgi", "Georgov", "go6o", ".Parola1", "George@gmail.com", "https://www.pngitem.com/pimgs/m/150-1503945_transparent-user-png-default-user-image-png-png.png", dev)
        );
//        List<Sprint> INIT_TASK = List.of(
//                new Sprint("11-12-2022", "12-23-2022",LocalDateTime.now(),null,)
//        );
        if (userService.getUsers().isEmpty()) {
            INIT_USERS.forEach(user -> userService.createUser(user));
        }
        if (sprintService.getAllSprints().isEmpty()) {
            sprintService.createSprint("pe6o", "", "", "", "1", "2022-01-22", "2023-01-22");
        }
        if (sprintResultService.getAllSprintResults().isEmpty()) {
            sprintResultService.createSprintResult("1", "10", "a lot of things", "");
        }
        if (taskService.getAllTasks().isEmpty()) {
            taskService.createTask("ACTIVE", "Task title", "RESEARCH", "description of task", "adminUsername", "go6o, adminUsername", "1", "10", "tags for the first",  "");
        }
        if (taskResultService.getAllTaskResults().isEmpty()) {
            List<Task> tasks = taskService.getAllTasks();
            taskResultService.createTaskResult("pe6o", "1", "10", "pe6o", "this is a description");
        }
        if (projectService.getAllProjects().isEmpty()) {
            projectService.createProject("title of project 1", "2021-01-22", "long project", "adminUsername", "go6o", "1", "", "", "tags for the project");
        }
        if (projectResultService.getAllProjectResults().isEmpty()) {
            projectResultService.createProjectResult("1", "", "", "description of results", "");
        }
        int i =1;
        i=2;
    }
}
