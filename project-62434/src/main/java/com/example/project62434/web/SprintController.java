package com.example.project62434.web;


import com.example.project62434.exceptions.CookieErrorException;
import com.example.project62434.models.Sprint;
import com.example.project62434.models.SprintResult;
import com.example.project62434.models.TaskResult;
import com.example.project62434.services.SprintResultService;
import com.example.project62434.services.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/sprints")
@RequiredArgsConstructor
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private SprintResultService sprintResultService;

    @GetMapping("/all")
    public String getAllSprints(Model model) {
        List<Sprint> sprints = sprintService.getAllSprints();
        model.addAttribute("sprints", sprints);
        return "sprints";
    }

    @GetMapping("/create")
    public String getCreateSprintPage(Model model) {
        return "sprint-create";
    }

    @PostMapping("/create")
    public String createProject(
            @RequestParam String owner,
            @RequestParam String developers,
            @RequestParam String tasks,
            @RequestParam String completedTaskResults,
            @RequestParam String duration,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        sprintService.createSprint(owner, developers, tasks, completedTaskResults, duration, startDate, endDate);
        return "redirect:/sprints/all";
    }

    @PostMapping("/create-result")
    public String createSprintResult(
            @RequestParam("task") String sprint,
            @RequestParam("actualEffort") String teamVelocity,
            @RequestParam("verifiedBy") String resultsDescription,
            @RequestParam("description") String taskResults,
            HttpServletRequest request) {
        sprintResultService.createSprintResult(sprint, teamVelocity, resultsDescription, taskResults);
        return "redirect:/tasks/all";
    }
    @GetMapping("/results")
    public String getSprintResults(Model model) {
        List<SprintResult> results = sprintResultService.getAllSprintResults();
        model.addAttribute("results", results);
        return "sprint-results";
    }
    @GetMapping("/results/create")
    public String getSprintResultCreate() {
        return "sprint-result-create";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            sprintService.deleteSprintById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteSprintResult(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            sprintResultService.deleteSprintResultById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
