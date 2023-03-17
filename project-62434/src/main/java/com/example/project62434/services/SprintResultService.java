package com.example.project62434.services;

import com.example.project62434.models.SprintResult;

import java.util.List;

public interface SprintResultService {
    SprintResult getSprintResultById(Long sprintResultId);

    List<SprintResult> getAllSprintResults();

    SprintResult createSprintResult(SprintResult sprintResult);

    SprintResult updateSprintResultById(Long sprintResultId, SprintResult sprintResult);

    void deleteSprintResultById(Long sprintResultId);

    SprintResult createSprintResult(String sprint, String teamVelocity, String resultsDescription, String taskResults);
}
