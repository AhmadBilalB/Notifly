package com.example.notifly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Permit access to send and send-email endpoints without authentication
                        .requestMatchers("/api/notifications/send", "/api/notifications/send-email").permitAll()
                        // Require authentication for other notification-related endpoints
                        .requestMatchers("/api/notifications/**").authenticated()
                        // Allow other requests without authentication
                        .anyRequest().permitAll()
                )
                .formLogin(withDefaults())  // Enable form login (if needed)
                .csrf(AbstractHttpConfigurer::disable);  // Disable CSRF for non-browser clients (Postman)

        return http.build();
    }
}