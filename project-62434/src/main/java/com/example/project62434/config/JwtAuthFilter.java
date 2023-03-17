package com.example.project62434.config;

import com.example.project62434.enums.Role;
import com.example.project62434.services.UserService;
import com.example.project62434.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Autowired
//    @Qualifier("UserService")
    private final UserService userService;

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String str = request.getServletPath();
        if (request.getServletPath().equals("/api/login")
                || request.getServletPath().matches("(.*\\.css|.*\\.js|.*\\.png|.*\\.jpg|/css.*|/js.*|/img.*|/font.*)")) {
            String s = request.getServletPath();
            s = s+"";
            filterChain.doFilter(request, response);
            return;
        }
        Cookie jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .orElse(null);
        if (jwtCookie == null) {
            response.sendRedirect("/api/login");
            return;
        }//Throw(() -> new ServletException("Missing JWT Cookie"));
        String token = jwtCookie.getValue();
//        final String authHeader = request.getHeader(AUTHORIZATION);
        final String username;

//
//        if (authHeader == null) {
////            filterChain.doFilter(request, response);
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (!authHeader.startsWith("Bearer")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

//        jwtToken = authHeader.substring(7);
        username = jwtUtils.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Set<Role> rolesSet = userService.getUserByUsername(username).getRole();
            if (jwtUtils.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, getAuthorities(rolesSet));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

}
