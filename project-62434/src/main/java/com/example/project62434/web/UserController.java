package com.example.project62434.web;

import com.example.project62434.dto.UserDto;
import com.example.project62434.models.User;
import com.example.project62434.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @GetMapping("/all")
    public String viewAllUsers(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
//        List<User> allUsers = userService.getUsers();
        List<UserDto> allUsers = userService.getUserDTOs();
        model.addAttribute("users", allUsers);
        return "users-view";
    }

    @GetMapping("/homepage")
    public String viewHomepage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        return "homepage";
    }

    @GetMapping("/all/role")
    public String viewUsersByRole(@RequestParam("role") String role, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/api/login";
        }
        List<User> usersByRole = userService.getUsersByRole(role);
        model.addAttribute("users", usersByRole);
        return "users-view";
    }

    @PostMapping("/homepage")
    public RedirectView updateInformation(
            @RequestParam("id") String id,
            @RequestParam("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("contacts") String contacts,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new RedirectView("/api/login");
        }
        User savedUser = userService.updateUserByIdWithPassword(Long.parseLong(id), firstName, lastName, contacts, username, oldPassword, password, email, null);
        session.setAttribute("user", savedUser);
        return new RedirectView("/api/users/homepage");
    }


    @PostMapping("/all")
    public RedirectView addUser(
            @RequestParam("id") String id,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("roles") String roles) {
        if (session.getAttribute("user") == null) {
            return new RedirectView("/api/login");
        }
        User savedUser;
        if (id == null || id.isEmpty()) {
            savedUser = userService.createUser(firstName, lastName, username, password, email, roles);
        } else {
            savedUser = userService.updateUserById(Long.parseLong(id), firstName, lastName, username, password, email, roles);
        }
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/all")
//                .buildAndExpand(savedUser.getId()).toUri();
        return new RedirectView("/api/users/all");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") != null) {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}