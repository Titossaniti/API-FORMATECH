package com.example.apiformatech.config;

import com.example.apiformatech.jwtfilter.JwtAuthenticationFilter;
import com.example.apiformatech.service.JwtService;
import com.example.apiformatech.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org. springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// Désactive la protection CSRF car on utilise des tokens JWT
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/auth/**").permitAll() // Authentification accessible sans token
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "SUPERADMIN") // Gestion des utilisateurs restreinte
                        .requestMatchers("/api/sessions/**").hasAnyRole("ADMIN", "SUPERADMIN") // Seuls superadmin et admin peuvent gérer les sessions
                        .requestMatchers("/api/modules/**").hasAnyRole("ADMIN", "SUPERADMIN", "TRAINER") // Les formateurs peuvent gérer leurs modules
                        .requestMatchers("/api/notes/**").hasAnyRole("TRAINER", "STUDENT") // Formateurs et étudiants peuvent consulter les notes

                        .anyRequest().authenticated())// Toutes les autres requêtes nécessitent une authentification
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))// Utilise des sessions stateless pour JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);// Ajoute le filtre JWT

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        return new JwtAuthenticationFilter(jwtService, userService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
