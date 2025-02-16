package com.example.apiformatech.controller;

import com.example.apiformatech.dto.SessionModuleDTO;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.service.SessionModuleService;
import com.example.apiformatech.model.Module;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
            @PathVariable Long sessionId,
            @PathVariable Long moduleId,
            @PathVariable Long trainerId,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Date startDate = java.sql.Date.valueOf(requestBody.get("startDate"));
            Date endDate = java.sql.Date.valueOf(requestBody.get("endDate"));

            SessionModule createdSessionModule = sessionModuleService.assignModuleToSession(
                    sessionId, moduleId, trainerId, startDate, endDate, userDetails);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdSessionModule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Récupérer toutes les relations session-module
    @GetMapping
    public ResponseEntity<List<SessionModuleDTO>> getAllSessionModules(@AuthenticationPrincipal UserDetails userDetails) {
        List<SessionModuleDTO> sessionModules = sessionModuleService.getAllSessionModules(userDetails);
        if (sessionModules.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(sessionModules);
    }

    // Récupérer les modules d'un formateur
    @GetMapping("/trainer/{trainerId}/modules")
    public ResponseEntity<List<Module>> getTrainerModules(@PathVariable Long trainerId) {
        List<Module> modules = sessionModuleService.getModulesByTrainer(trainerId);
        return ResponseEntity.ok(modules);
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
