package com.example.project62434.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull
    @NotNull
    @Size(min = 2, max = 120)
    private String title;

    private LocalDateTime startDate = LocalDateTime.now();

    @NonNull
    @NotNull
    @Size(min = 10, max = 2500)
    private String description;

//    @ManyToOne(fetch = FetchType.EAGER, optional = false, mappedBy = "")
//    @JoinColumn(name = "product_owner_id", foreignKey = @ForeignKey(name = "fk_project_owner"))
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "product_owner_id", foreignKey = @ForeignKey(name = "fk_project_owner"), nullable = true)
    private User owner;

    @OneToMany
    @JoinColumn(name = "project_id")
    private List<User> developers = new ArrayList<>();

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_sprint_id")
    private Sprint currentSprint;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private List<SprintResult> previousSprintResults = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private List<Task> tasksBacklog = new ArrayList<>();

    @NonNull
    @NotNull
    private String tags;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    public Project (String title, String description, String tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return title;
    }

}
