package com.example.project62434.web;

import com.example.project62434.dto.AuthenticationRequest;
import com.example.project62434.dto.UserDto;
import com.example.project62434.exceptions.EntityNotFoundException;
import com.example.project62434.exceptions.InvalidEntityDataException;
import com.example.project62434.models.User;
import com.example.project62434.services.AuthService;
import com.example.project62434.services.UserService;
import com.example.project62434.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpSession session;
    private final AuthService authService;

    //    @PostMapping("/login")
//    public ResponseEntity<String> login(@Valid @RequestBody AuthenticationRequest authenticationRequest,
//                                        Errors errors) {
//        if (errors.hasErrors()) {
//            throw new InvalidEntityDataException("Invalid data", errors.getAllErrors().stream()
//                    .map(ObjectError::toString)
//                    .collect(Collectors.toList()));
//        }
//
//        UserDetails user;
//
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
//            user = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//        } catch (AuthenticationException exception) {
//            return ResponseEntity.status(401).body(exception.getMessage());
//        } catch (EntityNotFoundException exception) {
//            return ResponseEntity.status(404).body(exception.getMessage());
//        }
//
//        return ResponseEntity.ok(jwtUtils.generateToken(user));
//    }
    @GetMapping("/login")
    public String getLoginForm(Model model) {
        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("password")) {
            model.addAttribute("password", "");
        }
        return "auth-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @ModelAttribute("redirectUrl") String redirectUrl,
                        HttpServletResponse response,
                        HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authService.login(username, password);
        }
        catch (RuntimeException e) {
            throw new IllegalArgumentException("The username or password is incorrect");
        }

        if (loggedUser == null) {
            String errors = "Invalid username or password.";
            return "redirect:/api/login";
        } else {
            String jwtToken = jwtUtils.generateToken(loggedUser);
            Cookie jwtCookie = new Cookie("token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(3600);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
            session.setAttribute("user", new UserDto(loggedUser));
            session.setAttribute("token", jwtToken);
            if(redirectUrl != null && redirectUrl.length() > 0 ) {
                return "redirect:" + redirectUrl;
            } else {
                return "redirect:/api/users/homepage";
            }
        }

    }

    @GetMapping("/print-session-variables")
    public void printSessionVariables() {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            System.out.println(name + ": " + value);
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) { // OR SessionStatus status
        Cookie jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .orElse(null);
        if (jwtCookie != null) {
            jwtCookie.setValue("");
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);
        }
        SecurityContextHolder.clearContext();
        session.invalidate();
//        status.setComplete();
        return "redirect:/api/login";
    }
}
