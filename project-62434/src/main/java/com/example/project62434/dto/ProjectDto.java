package com.example.project62434.dto;

import com.example.project62434.models.Sprint;
import com.example.project62434.models.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private String description;
    private User owner;
    private List<User> developers;
    private String tags;
    private Sprint currentSprint;
    private LocalDateTime created;
    private LocalDateTime modified;
}