package com.example.project62434.web;


import com.example.project62434.dto.ProjectDto;
import com.example.project62434.models.Project;
import com.example.project62434.models.ProjectResult;
import com.example.project62434.models.TaskResult;
import com.example.project62434.services.ProjectResultService;
import com.example.project62434.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectResultService projectResultService;

    @GetMapping("/all")
    @Transactional
    public String getAllProjects(Model model) {
//        List<Project> projects = projectService.getAllProjects();
//        List<ProjectDto> projectDtos = projects.stream().map(p -> projectService.convertToDto(p)).collect(Collectors.toList());
        List<Project> projects = projectService.getAllProjects();
//        for (Project project : projects) {
//            project.getDevelopers().size();
//            project.getTasksBacklog().size();
//            project.getPreviousSprintResults().size();
//        }
        model.addAttribute("projects", projects);

        return "projects";
    }

    @GetMapping("/create")
    public String getCreateProjectsPage(Model model) {
        return "project-create";
    }

    @PostMapping("/create")
    public String createProject(
            @RequestParam String title,
            @RequestParam String startDate,
            @RequestParam String description,
            @RequestParam String owner,
            @RequestParam String developers,
            @RequestParam String currentSprint,
            @RequestParam String previousSprintResults,
            @RequestParam String tasksBacklog,
            @RequestParam String tags) {
        projectService.createProject(title, startDate, description, owner, developers, currentSprint,previousSprintResults, tasksBacklog, tags);
        return "redirect:/projects/all";
    }

    @GetMapping("/results")
    public String getProjectResults(Model model) {
        List<ProjectResult> results = projectResultService.getAllProjectResults();
        model.addAttribute("results", results);
        return "project-results";
    }

    @GetMapping("/results/create")
    public String getProjectResultCreateForm(Model model) {
        return "project-result-create";
    }

    @PostMapping("/results/create")
    public String submitProjectResult(@RequestParam("project") String project,
                                      @RequestParam("endDate") String endDate,
                                      @RequestParam("duration") String duration,
                                      @RequestParam("resultsDescription") String resultsDescription,
                                      @RequestParam("sprintResults") String sprintResults) {
        projectResultService.createProjectResult(project, endDate, duration, resultsDescription, sprintResults);
        return "project-result-create";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            projectService.deleteProjectById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteProjectResult(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            projectResultService.deleteProjectResultById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }


}
