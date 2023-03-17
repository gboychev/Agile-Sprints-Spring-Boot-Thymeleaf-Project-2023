package com.example.project62434.config;

import com.example.project62434.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/all").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/users/all").hasRole("ADMIN")
//                .antMatchers("/api/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/projects/all").permitAll()
                .antMatchers(HttpMethod.POST, "/projects/all").hasRole("PRODUCT_OWNER")
                .antMatchers("/sprints/create").hasAnyRole("PRODUCT_OWNER", "DEVELOPER")
                .antMatchers(HttpMethod.POST, "/sprints/all").hasAnyRole("PRODUCT_OWNER", "DEVELOPER")
                .antMatchers("/tasks/create").hasAnyRole("PRODUCT_OWNER", "DEVELOPER")
                .antMatchers(HttpMethod.POST, "/tasks/all").hasAnyRole("PRODUCT_OWNER", "DEVELOPER")
                .antMatchers("/projects/create").hasRole("ADMIN")
                .antMatchers("/projects/results/create").hasRole("ADMIN")
                .antMatchers("/sprints/all").permitAll()
                .antMatchers("/tasks/**").permitAll()//hasAuthority(Role.ADMIN.name())
//                .antMatchers("/**").permitAll()//hasAnyAuthority(Role.ADMIN.name(), Role.DEVELOPER.name())
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
