package com.example.apiformatech.controller;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Crée une nouvelle session de formation.
     * @param session L'objet session à créer.
     * @return La session créée.
     */
    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session savedSession = sessionService.saveSession(session);
        return ResponseEntity.ok(savedSession);
    }

    /**
     * Récupère toutes les sessions de formation.
     * @return La liste des sessions.
     */
    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.getAllSessions();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        Optional<Session> session = sessionService.findById(id);
        return session.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    /**
     * Supprime une session de formation par ID.
     * @param id L'ID de la session à supprimer.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
