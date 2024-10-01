package com.example.apiformatech.controller;

import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.service.SessionModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-with-modules")
public class SessionModuleController {

    private final SessionModuleService sessionModuleService;

    public SessionModuleController(SessionModuleService sessionModuleService) {
        this.sessionModuleService = sessionModuleService;
    }

    // Assigner un module à une session avec un formateur
    @PostMapping("/{sessionId}/modules/{moduleId}/trainers/{trainerId}")
    public ResponseEntity<SessionModule> assignModuleToSession(
            @PathVariable Long sessionId, @PathVariable Long moduleId, @PathVariable Long trainerId) {
        try {
            SessionModule sessionModule = sessionModuleService.assignModuleToSession(sessionId, moduleId, trainerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(sessionModule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Récupérer toutes les relations session-module
    @GetMapping
    public ResponseEntity<List<SessionModule>> getAllSessionModules() {
        List<SessionModule> sessionModules = sessionModuleService.getAllSessionModules();
        if (sessionModules.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(sessionModules);
    }


    // Supprimer une relation session-module
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionModule(@PathVariable Long id) {
        try {
            sessionModuleService.deleteSessionModule(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
