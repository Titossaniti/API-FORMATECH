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

    // Créer ou mettre à jour un module
    @PostMapping
    public ResponseEntity<Module> createOrUpdateModule(@RequestBody Module module) {
        Module savedModule = moduleService.saveModule(module);
        return ResponseEntity.ok(savedModule);
    }

    // Récupérer tous les modules
    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    // Récupérer un module par ID
    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour un module
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable Long id, @RequestBody Module module) {
        Module updatedModule = moduleService.updateModule(id, module);
        return ResponseEntity.ok(updatedModule);
    }

    // Supprimer un module
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}


