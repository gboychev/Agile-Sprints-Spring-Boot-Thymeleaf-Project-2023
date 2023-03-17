package com.example.project62434.services;

import com.example.project62434.dto.ProjectDto;
import com.example.project62434.models.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectService {
    Project getProjectById(Long projectId);

    List<Project> getAllProjects();

    @Transactional
    List<ProjectDto> getAllProjectDtos();

    Project createProject(Project project);

    Project updateProjectById(Long projectId, Project project);

    void deleteProjectById(Long projectId);

    ProjectDto convertToDto(Project project);

    Project updateProjectById(Long id, String title, String startDate, String description, String owner, String developers, String currentSprint, String previousSprintResults, String tasksBacklog, String tags);

    Project createProject(String title, String startDate, String description, String owner, String developers, String currentSprint, String previousSprintResults, String tasksBacklog, String tags);

    @Transactional
    Project updateTasksBacklogById(Long id, String tasksBacklogIDs);


    @Transactional
    Project updatePreviousSprintResultsById(Long id, String previousSprintResults);

    @Transactional
    Project updateProjectDevelopersById(Long id, String developers);
}
