package com.example.apiformatech.controller;

import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.service.SessionUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session-users")
public class SessionUserController {

    private final SessionUserService sessionUserService;

    public SessionUserController(SessionUserService sessionUserService) {
        this.sessionUserService = sessionUserService;
    }

    // Ajouter un élève à une session
    @PostMapping("/{sessionId}/students/{userId}")
    public ResponseEntity<SessionUser> addStudentToSession(@PathVariable Long userId, @PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionUserService.addStudentToSession(userId, sessionId));
    }

    // Endpoint pour retirer un élève d’une session
    @DeleteMapping("/{sessionId}/students/{userId}")
    public ResponseEntity<Void> removeStudentFromSession(@PathVariable Long userId, @PathVariable Long sessionId) {
        sessionUserService.removeStudentFromSession(userId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
