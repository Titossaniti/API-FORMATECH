package com.example.apiformatech.controller;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.service.SessionService;
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
    public ResponseEntity<Session> createOrUpdateSession(@RequestBody Session session) {
        Session savedSession = sessionService.saveSession(session);
        return ResponseEntity.ok(savedSession);
    }

    // Récupérer toutes les sessions
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    // Récupérer une session par ID
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Assigner une session à un établissement
    @PutMapping("/{sessionId}/establishment/{establishmentId}")
    public ResponseEntity<Session> assignSessionToEstablishment(@PathVariable Long sessionId, @PathVariable Long establishmentId) {
        Session updatedSession = sessionService.assignSessionToEstablishment(sessionId, establishmentId);
        return ResponseEntity.ok(updatedSession);
    }

    // Mettre à jour une session
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @RequestBody Session session) {
        Session updatedSession = sessionService.updateSession(id, session);
        return ResponseEntity.ok(updatedSession);
    }

    // Supprimer une session
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
