package com.example.apiformatech.config;

import com.example.apiformatech.jwtfilter.JwtAuthenticationFilter;
import com.example.apiformatech.service.JwtService;
import com.example.apiformatech.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    public SecurityConfig(@Lazy UserService userService) {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// Désactive la protection CSRF car on utilise des tokens JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll() // Authentification accessible sans token
//                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated() // Modifier son profil si on est connecté
//                        .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Gestion des utilisateurs restreinte
//                        .requestMatchers("/api/sessions/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Seuls superadmin et admin peuvent gérer les sessions
//                        .requestMatchers("/api/sessions/**").authenticated()
//                        .requestMatchers("/api/modules/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Seuls superadmin et admin peuvent gérer leurs modules
//                        .requestMatchers("/api/notes/**").hasAnyAuthority("TRAINER", "STUDENT") // Formateurs et étudiants peuvent consulter les notes
//                        .requestMatchers(HttpMethod.POST, "/api/sessions/{sessionId}/modules/{moduleId}/trainers/{trainerId}").hasAnyAuthority("SUPERADMIN", "ADMIN") // SAdmin et admin peuvent associer session/module/formateur

                        .anyRequest().authenticated())// Toutes les autres requêtes nécessitent une authentification
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))// Utilise des sessions stateless pour JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);// Ajoute le filtre JWT

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Autoriser Angular
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
