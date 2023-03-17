package com.example.project62434.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
//
//@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime endDate = LocalDateTime.now();

    private int duration;

    public Sprint(LocalDateTime created, LocalDateTime modified, int duration, LocalDateTime startDate, LocalDateTime endDate) {
        this.created = created;
        this.modified = modified;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany
    @JoinColumn(name = "sprint_id")
    private Set<User> developers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sprint", fetch = FetchType.LAZY)
    private Set<Task> tasks;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private List<TaskResult> completedTaskResults;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<User> developers) {
        this.developers = developers;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public List<TaskResult> getCompletedTaskResults() {
        return completedTaskResults;
    }

    public void setCompletedTaskResults(List<TaskResult> completedTaskResults) {
        this.completedTaskResults = completedTaskResults;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

}
