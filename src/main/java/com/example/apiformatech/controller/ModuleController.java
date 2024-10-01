package com.example.apiformatech.controller;

import com.example.apiformatech.model.Module;
import com.example.apiformatech.service.ModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * Crée un nouveau module.
     * @param module L'objet module à créer.
     * @return Le module créé.
     */
    @PostMapping
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        Module savedModule = moduleService.saveModule(module);
        return ResponseEntity.ok(savedModule);
    }

    /**
     * Récupère tous les modules.
     *
     * @return La liste des modules.
     */
    @GetMapping
    public List<Module> getAllModules() {
        return moduleService.getAllModules();
    }

    /**
     * Supprime un module par ID.
     * @param id L'ID du module à supprimer.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}

