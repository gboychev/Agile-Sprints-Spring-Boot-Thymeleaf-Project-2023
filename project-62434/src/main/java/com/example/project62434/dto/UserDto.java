package com.example.project62434.dto;

import com.example.project62434.enums.Role;
import com.example.project62434.enums.Status;
import com.example.project62434.models.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String imageUrl;
    private Set<Role> role;
    private Set<String> contacts;
    private Status status;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    List<Task> assignedTasksForDeveloper;
    List<TaskResult> completedTaskResultsForDeveloper;
    List<Project> projectsForProductOwner;
    List<ProjectResult> completedProjectResultsForProductOwner;
    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.role = user.getRole();
        this.contacts = user.getContacts();
        this.status = user.getStatus();
        this.created = user.getCreated();
        this.modified = user.getModified();
        this.assignedTasksForDeveloper = user.getAssignedTasksForDeveloper();
        this.completedTaskResultsForDeveloper = user.getCompletedTaskResultsForDeveloper();
        this.projectsForProductOwner = user.getProjectsForProductOwner();
        this.completedProjectResultsForProductOwner = user.getCompletedProjectResultsForProductOwner();
    }
}
