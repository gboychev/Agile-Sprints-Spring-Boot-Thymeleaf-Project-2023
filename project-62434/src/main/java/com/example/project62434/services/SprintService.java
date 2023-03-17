package com.example.project62434.services;

import com.example.project62434.models.Sprint;

import java.util.List;

public interface SprintService {
    Sprint getSprintById(Long sprintId);

    List<Sprint> getAllSprints();


    Sprint createSprint(Sprint sprint);

    Sprint updateSprintById(Long sprintId, Sprint sprint);

    void deleteSprintById(Long sprintId);

    Sprint createSprint(String owner, String developers, String tasks, String completedTaskResults, String duration, String startDate, String endDate);
}
