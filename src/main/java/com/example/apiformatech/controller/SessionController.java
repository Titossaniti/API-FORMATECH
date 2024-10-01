package com.example.apiformatech.controller;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // Créer ou mettre à jour une session
    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session savedSession = sessionService.saveSession(session);
        if (session.getId() == null) {
            // Création d'une nouvelle session
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
        } else {
            // Mise à jour d'une session existante
            return ResponseEntity.ok(savedSession);
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


    // Assigner une session à un établissement
    @PutMapping("/{sessionId}/establishment/{establishmentId}")
    public ResponseEntity<Session> assignSessionToEstablishment(@PathVariable Long sessionId, @PathVariable Long establishmentId) {
        try {
            Session updatedSession = sessionService.assignSessionToEstablishment(sessionId, establishmentId);
            return ResponseEntity.ok(updatedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Mettre à jour une session
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @RequestBody Session session) {
        try {
            Session updatedSession = sessionService.updateSession(id, session);
            return ResponseEntity.ok(updatedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Supprimer une session
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
