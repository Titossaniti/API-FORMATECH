package com.example.apiformatech.controller;

import com.example.apiformatech.model.Module;
import com.example.apiformatech.service.ModuleService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        Module savedModule = moduleService.saveModule(module);
        if (module.getId() == null) {
            // Si l'ID est null, c'est une création
            return ResponseEntity.status(HttpStatus.CREATED).body(savedModule);
        } else {
            // Sinon, c'est une mise à jour
            return ResponseEntity.ok(savedModule);
        }
    }


    // Récupérer tous les modules
    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        List<Module> modules = moduleService.getAllModules();
        if (modules.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(modules);
    }


    // Récupérer un module par ID
    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Mettre à jour un module
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable Long id, @RequestBody Module module) {
        try {
            Module updatedModule = moduleService.updateModule(id, module);
            return ResponseEntity.ok(updatedModule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Supprimer un module
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        try {
            moduleService.deleteModule(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}


