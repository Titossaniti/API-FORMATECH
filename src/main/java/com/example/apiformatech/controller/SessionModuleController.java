package com.example.apiformatech.controller;

import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.service.SessionModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-modules")
public class SessionModuleController {

    private final SessionModuleService sessionModuleService;

    public SessionModuleController(SessionModuleService sessionModuleService) {
        this.sessionModuleService = sessionModuleService;
    }

    // Assigner un module à une session avec un formateur
    @PostMapping("/{sessionId}/modules/{moduleId}/trainers/{trainerId}")
    public ResponseEntity<SessionModule> assignModuleToSession(
            @PathVariable Long sessionId, @PathVariable Long moduleId, @PathVariable Long trainerId) {
        SessionModule sessionModule = sessionModuleService.assignModuleToSession(sessionId, moduleId, trainerId);
        return ResponseEntity.ok(sessionModule);
    }

    // Récupérer toutes les relations session-module
    @GetMapping
    public ResponseEntity<List<SessionModule>> getAllSessionModules() {
        return ResponseEntity.ok(sessionModuleService.getAllSessionModules());
    }

    // Supprimer une relation session-module
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionModule(@PathVariable Long id) {
        sessionModuleService.deleteSessionModule(id);
        return ResponseEntity.noContent().build();
    }
}
