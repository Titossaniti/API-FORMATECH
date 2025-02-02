package com.example.apiformatech.controller;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.User;
import com.example.apiformatech.service.SessionService;
import com.example.apiformatech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody Session session,
                                           @RequestParam Long establishmentId,
                                           @AuthenticationPrincipal User user) {
        try {
            Session createdSession = sessionService.createSession(session, establishmentId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Récupérer toutes les sessions
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = sessionService.getAllSessions();
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
    public ResponseEntity<?> deleteSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            sessionService.deleteSession(id, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
