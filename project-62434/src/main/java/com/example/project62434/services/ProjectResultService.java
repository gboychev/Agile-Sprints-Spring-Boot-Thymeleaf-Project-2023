package com.example.project62434.services;

import com.example.project62434.models.ProjectResult;

import java.util.List;

public interface ProjectResultService {
    ProjectResult getProjectResultById(Long projectResultId);

    List<ProjectResult> getAllProjectResults();

    ProjectResult createProjectResult(ProjectResult projectResult);

    ProjectResult updateProjectResultById(Long projectResultId, ProjectResult projectResult);

    void deleteProjectResultById(Long projectResultId);

    ProjectResult createProjectResult(String project, String endDate, String duration, String resultsDescription, String sprintResults);
}
