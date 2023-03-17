package com.example.project62434.services.impl;

import com.example.project62434.models.Project;
import com.example.project62434.models.ProjectResult;
import com.example.project62434.models.Sprint;
import com.example.project62434.respositories.ProjectResultRepository;
import com.example.project62434.services.ProjectResultService;
import com.example.project62434.services.ProjectService;
import com.example.project62434.services.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectResultServiceImpl implements ProjectResultService {

    private ProjectResultRepository projectResultRepository;

    private ProjectService projectService;

    private SprintService sprintService;

    @Autowired
    public ProjectResultServiceImpl(ProjectResultRepository projectResultRepository,@Lazy ProjectService projectService, @Lazy SprintService sprintService) {
        this.projectService = projectService;
        this.projectResultRepository = projectResultRepository;
        this.sprintService = sprintService;
    }
    @Override
    public ProjectResult getProjectResultById(Long projectResultId) {
        return projectResultRepository.getReferenceById(projectResultId);
    }

    @Override
    public List<ProjectResult> getAllProjectResults() {
        return projectResultRepository.findAll();
    }

    @Override
    public ProjectResult createProjectResult(ProjectResult projectResult) {
        return projectResultRepository.save(projectResult);
    }

    @Override
    public ProjectResult updateProjectResultById(Long projectResultId, ProjectResult projectResult) {

        return null;
    }

    @Override
    public void deleteProjectResultById(Long projectResultId) {
        projectResultRepository.deleteById(projectResultId);
    }

    @Override
    @Transactional
    public ProjectResult createProjectResult(String project, String endDate, String duration, String resultsDescription, String sprintResults) {
        Project projectAssociatedWithProjectResult = projectService.getProjectById(Long.parseLong(project));
        ProjectResult projectResult = new ProjectResult();
        projectResult.setProject(projectAssociatedWithProjectResult);
        projectResult.setEndDate(LocalDateTime.now());
        projectResult.setDuration((int)Duration.between(projectAssociatedWithProjectResult.getStartDate(), projectResult.getEndDate()).toDays());
        projectResult.setResultsDescription(resultsDescription);

        projectResult.setSprintResults(new HashSet<>(projectAssociatedWithProjectResult.getPreviousSprintResults()));
        return projectResultRepository.save(projectResult);
    }
}
