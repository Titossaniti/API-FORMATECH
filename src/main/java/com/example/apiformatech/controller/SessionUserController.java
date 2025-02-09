package com.example.apiformatech.controller;

import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.model.User;
import com.example.apiformatech.service.SessionUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-users")
public class SessionUserController {

    private final SessionUserService sessionUserService;

    public SessionUserController(SessionUserService sessionUserService) {
        this.sessionUserService = sessionUserService;
    }

    // Récupérer les utilisateurs d'une session en fonction du rôle de l'utilisateur connecté
    @GetMapping("/{sessionId}")
    public ResponseEntity<List<User>> getUsersBySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<User> users = sessionUserService.getUsersBySession(sessionId, userDetails);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(users);
    }

    // Ajouter un élève à une session
    @PostMapping("/{sessionId}/students/{userId}")
    public ResponseEntity<SessionUser> addStudentToSession(@PathVariable Long userId, @PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionUserService.addStudentToSession(userId, sessionId));
    }

    // Retirer un élève d’une session
    @DeleteMapping("/{sessionId}/students/{userId}")
    public ResponseEntity<Void> removeStudentFromSession(@PathVariable Long userId, @PathVariable Long sessionId) {
        sessionUserService.removeStudentFromSession(userId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
