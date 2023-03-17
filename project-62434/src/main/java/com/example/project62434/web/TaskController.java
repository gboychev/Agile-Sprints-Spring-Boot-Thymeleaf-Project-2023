package com.example.project62434.web;


import com.example.project62434.exceptions.CookieErrorException;
import com.example.project62434.models.Task;
import com.example.project62434.models.TaskResult;
import com.example.project62434.services.TaskResultService;
import com.example.project62434.services.TaskService;
import com.example.project62434.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    TaskResultService taskResultService;

    private final JwtUtils jwtUtils;

    @GetMapping("/all")
    public String getAllTasks(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/create")
    public String getCreateTaskPage(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        return "task-create";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id, @RequestParam String num) {
        taskService.updateTaskStatusById(id, num);
        return "redirect:/tasks/all?timestamp=" + System.currentTimeMillis();
    }


    @PostMapping("/create")
    public String createTask(
            @RequestParam String status,
            @RequestParam String title,
            @RequestParam String kind,
            @RequestParam String description,
            @RequestParam String addedBy,
            @RequestParam String developers,
            @RequestParam String sprint,
            @RequestParam String estimatedEffort,
            @RequestParam String tags,
            HttpServletRequest request, HttpSession session) {
            if (session.getAttribute("user") == null) {
                return "redirect:/api/login";
            }
        Cookie jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .orElse(null);
        if (jwtCookie == null) {
            throw new CookieErrorException("No cookie when requesting to create a task result");
        }
        String token = jwtCookie.getValue();
        String username = jwtUtils.extractUsername(token);
        taskService.createTask(status, title, kind, description, addedBy, developers, sprint, estimatedEffort, tags, username);
        return "redirect:/tasks/all";
    }

    @GetMapping("/create-result")
    public String getCreateResult(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        return "task-create-result";
    }

    @GetMapping("/results")
    public String getTaskResults(Model model, HttpSession session) {
            if (session.getAttribute("user") == null) {
                return "redirect:/api/login";
            }
        List<TaskResult> results = taskResultService.getAllTaskResults();
        model.addAttribute("results", results);
        return "task-results";
    }

    @PostMapping("/create-result")
    public String createTaskResult(
            @RequestParam("task") String task,
            @RequestParam("actualEffort") String actualEffort,
            @RequestParam("verifiedBy") String verifiedBy,
            @RequestParam("description") String description,
            HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        Cookie jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .orElse(null);
        if (jwtCookie == null) {
            throw new CookieErrorException("No cookie when requesting to create a task result");
        }
        String token = jwtCookie.getValue();
        String username = jwtUtils.extractUsername(token);
        taskResultService.createTaskResult(username, task, actualEffort, verifiedBy, description);
        return "redirect:/tasks/all";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            taskService.deleteTaskById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteTaskResult(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            taskResultService.deleteTaskResultById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
