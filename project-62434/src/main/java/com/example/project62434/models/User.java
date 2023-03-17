package com.example.project62434.models;

import com.example.project62434.enums.Role;
import com.example.project62434.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class User {

    public User(String firstName, String lastName, String username, String password, String email, String imageUrl, Set<Role> role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
        this.status = Status.ACTIVE;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull
    @Size(min = 2, max = 15)
    private String firstName;

    @NonNull
    @NotNull
    @Size(min = 2, max = 15)
    private String lastName;

    @NonNull
    @NotNull
    @Size(min = 2, max = 15)
    @Column(name = "username", unique = true)
    private String username;

    @NonNull
    @NotNull
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#%@!^&*?|/._\\-=]).{8,15}$", message = "Wrong password format.")
    @Size(min = 2)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @NonNull
    @NotNull
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @ToString.Exclude
    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> role = new HashSet<>();

    @ToString.Exclude
    @NotNull
    @NonNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> contacts = new HashSet<>();

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();


    //developer stuff
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "developer_task",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> assignedTasksForDeveloper = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "developer_task_result",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "task_result_id"))
    private List<TaskResult> completedTaskResultsForDeveloper = new ArrayList<>();

    //Product Owner Stuff
    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Project> projectsForProductOwner = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_owner_id")
    private List<ProjectResult> completedProjectResultsForProductOwner = new ArrayList<>();

}
