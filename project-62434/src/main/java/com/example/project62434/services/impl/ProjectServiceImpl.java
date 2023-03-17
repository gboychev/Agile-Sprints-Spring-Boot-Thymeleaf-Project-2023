package com.example.project62434.services.impl;

import com.example.project62434.dto.ProjectDto;
import com.example.project62434.exceptions.EntityNotFoundException;
import com.example.project62434.models.*;
import com.example.project62434.respositories.ProjectRepository;
import com.example.project62434.respositories.SprintResultRepository;
import com.example.project62434.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private SprintResultService sprintResultService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;
    private final SprintResultRepository sprintResultRepository;

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " was not found in the database"));
    }

    @Override
    @Transactional
    public List<Project> getAllProjects() {
        List<Project> result = projectRepository.findAll();
        for (Project project : result) {
            project.getPreviousSprintResults().size();
            project.getDevelopers().size();
            project.getTasksBacklog().size();
        }
        return result;
    }

    @Override
    @Transactional
    public List<ProjectDto> getAllProjectDtos() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = projects.stream().map(p -> convertToDto(p)).toList();
//        for (ProjectDto p : projectDtos) {
//            p.getDevelopers().size();
//            p.get
//        }
        return projectDtos;
    }

    @Override
    @Transactional
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project updateProjectById(Long projectId, Project project) {
        return null;
    }

    @Override
    @Transactional
    public void deleteProjectById(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Override
    @Transactional
    public ProjectDto convertToDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setOwner(project.getOwner());
        projectDto.setTitle(project.getTitle());
        projectDto.setStartDate(project.getStartDate());
        projectDto.setDescription(project.getDescription());
        projectDto.setDevelopers(project.getDevelopers());
        projectDto.setTags(project.getTags());
        projectDto.setCreated(project.getCreated());
        projectDto.setModified(project.getModified());
        projectDto.setCurrentSprint(project.getCurrentSprint());
        return projectDto;
    }


    @Override
    public Project updateProjectById(Long id, String title, String startDate, String description, String owner, String developers, String currentSprint, String previousSprintResults, String tasksBacklog, String tags) {
        Project project = getProjectById(id);
        if (title != null && title.isEmpty()) {
            project.setTitle(title);
        }
        if (description != null && description.isEmpty()) {
            project.setDescription(description);
        }
        if (owner != null && owner.isEmpty()) {
            project.setOwner(userService.getOwnerByUserName(owner));
        }
        if (developers != null && developers.isEmpty()) {
            updateProjectDevelopers(project, developers);
        }
        if (currentSprint != null && currentSprint.isEmpty()) {
            Sprint currentSprintInstance = sprintService.getSprintById(Long.parseLong(currentSprint));
            project.setCurrentSprint(currentSprintInstance);
        }
        if (previousSprintResults != null && previousSprintResults.isEmpty()) {
            updatePreviousSprintResults(project, previousSprintResults);
        }
        if (tasksBacklog != null && tasksBacklog.isEmpty()) {
            updateTasksBacklog(project, tasksBacklog);
        }
        if (tags != null && tags.isEmpty()) {
            project.setTags(tags);
        }
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public Project createProject(String title, String startDate, String description, String owner, String developers, String currentSprint, String previousSprintResults, String tasksBacklog, String tags) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Project project = new Project(title, description, tags);
        LocalDateTime now = LocalDateTime.now();
        project.setCreated(now);
        project.setModified(now);
        project.setDescription(description);
        project.setStartDate(LocalDate.parse(startDate, formatter).atStartOfDay());//how does this work?
        if (owner != null && !owner.isEmpty()) {
            project.setOwner(userService.getOwnerByUserName(owner));
        }
        if (tasksBacklog != null && !tasksBacklog.isEmpty()) {
            updateTasksBacklog(project, tasksBacklog);
        }
        if (previousSprintResults != null && !previousSprintResults.isEmpty()) {
            updatePreviousSprintResults(project, previousSprintResults);
        }
        if (developers != null && !developers.isEmpty()) {
            updateProjectDevelopers(project, developers);
        }
        if (currentSprint != null && !currentSprint.isEmpty()) {
            Sprint currentSprintInstance = sprintService.getSprintById(Long.parseLong(currentSprint));
            project.setCurrentSprint(currentSprintInstance);
        }
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project updateTasksBacklog(Project project, String tasksBacklogIDs) {
        List<Task> taskBacklogList = new ArrayList<>();
        String[] taskBacklogArr = tasksBacklogIDs.split(",");
        for (String s : taskBacklogArr) {
            s.trim();
            taskBacklogList.add(taskService.getTaskById(Long.parseLong(s)));
        }
        project.setTasksBacklog(taskBacklogList);
        return project;
    }

    @Override
    @Transactional
    public Project updateTasksBacklogById(Long id, String tasksBacklogIDs) {
        Project project = getProjectById(id);
        return projectRepository.save(updateTasksBacklog(project, tasksBacklogIDs));
    }

    @Transactional(readOnly = true)
    public Project updatePreviousSprintResults(Project project, String previousSprintResults) {
        List<SprintResult> previousSprintResultsList = new ArrayList<>();
        String[] previousSprintResultsArr = previousSprintResults.split(",");
        for (String s : previousSprintResultsArr) {
            s.trim();
            previousSprintResultsList.add(sprintResultService.getSprintResultById(Long.parseLong(s)));
        }
        project.setPreviousSprintResults(previousSprintResultsList);
        return project;
    }

    @Override
    @Transactional
    public Project updatePreviousSprintResultsById(Long id, String previousSprintResults) {
        Project project = getProjectById(id);
        return projectRepository.save(updatePreviousSprintResults(project, previousSprintResults));
    }

    @Transactional(readOnly = true)
    public Project updateProjectDevelopers(Project project, String developers) {
        List<User> developersSet = new ArrayList<>();
        String[] developerArr = developers.split(",");
        for (String s : developerArr) {
            s.trim();
            User dev = userService.getDeveloperByUsername(s);
            developersSet.add(dev);
        }
        project.setDevelopers(developersSet);
        return project;
    }

    @Override
    @Transactional
    public Project updateProjectDevelopersById(Long id, String developers) {
        Project project = getProjectById(id);
        return projectRepository.save(updateProjectDevelopers(project, developers));
    }

}
