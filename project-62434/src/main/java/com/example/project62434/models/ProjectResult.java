package com.example.project62434.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class ProjectResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Override
    public String toString() {
        return id.toString();
    }
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "product_owner_id", foreignKey = @ForeignKey(name = "fk_project_result_owner"))
//    private User owner;


    @PastOrPresent
    private LocalDateTime endDate = LocalDateTime.now();

//    @NonNull
    @NotNull
    private int duration;

    @Size(min = 10, max = 2500)
    private String resultsDescription;

    @OneToMany
    @JoinColumn(name = "project_result_id")
    private Set<SprintResult> sprintResults;


    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();
}
