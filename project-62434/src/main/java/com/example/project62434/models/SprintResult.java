package com.example.project62434.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class SprintResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

//    @NonNull
    @NotNull
    private int teamVelocity;

    @Override
    public String toString() {
        return id.toString();
    }

    private String resultsDescription;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sprint_result_id")
    private List<TaskResult> tasksResults = new ArrayList<>();

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();
}
