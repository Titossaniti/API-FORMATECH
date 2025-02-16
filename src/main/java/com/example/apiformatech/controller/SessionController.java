package com.example.apiformatech.controller;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.User;
import com.example.apiformatech.service.SessionService;
import com.example.apiformatech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    // Créer une session en la liant directement à un établissement
    @PostMapping("/establishment/{establishmentId}")
    public ResponseEntity<?> createSession(@PathVariable Long establishmentId,
                                           @RequestBody Session session,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Récupération de l'utilisateur à partir de son email
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // Créer la session
            Session createdSession = sessionService.createSession(session, establishmentId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Récupérer les sessions en fonction du rôle
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions(@AuthenticationPrincipal UserDetails userDetails) {
        // Récupération de l'utilisateur connecté
        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<Session> sessions;

        if (user.getRole().getTitle().equals("SUPERADMIN")) {
            // Le superadmin récupère toutes les sessions
            sessions = sessionService.getAllSessions();
        } else if (user.getRole().getTitle().equals("ADMIN")) {
            // Un admin ne voit que les sessions de son établissement
            if (user.getEstablishment() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            sessions = sessionService.getSessionsByEstablishment(user.getEstablishment().getId());
        } else if (user.getRole().getTitle().equals("TRAINER")) {
            // Un formateur voit uniquement les sessions des modules qu'il anime
            sessions = sessionService.getTrainerSessions(user.getId());
        } else if (user.getRole().getTitle().equals("STUDENT")) {
            // Un étudiant voit uniquement les sessions où il est inscrit
            sessions = sessionService.getStudentSessions(user.getId());
        } else {
            // Tout autre utilisateur n'a pas accès aux sessions
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (sessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(sessions);
    }

    // Récupérer une session par ID
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Mettre à jour une session (Admin peut modifier uniquement celles de son établissement)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id,
                                           @RequestBody Session session,
                                           @AuthenticationPrincipal User user) {
        try {
            Session updatedSession = sessionService.updateSession(id, session, user);
            return ResponseEntity.ok(updatedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Supprimer une session (Admin uniquement dans son établissement)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            sessionService.deleteSession(id, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
