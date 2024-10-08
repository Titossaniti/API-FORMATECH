package com.example.apiformatech.service;


import com.example.apiformatech.model.Module;
import com.example.apiformatech.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    // Injection des dépendances via le constructeur
    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    // Méthode pour sauvegarder un module
    public Module saveModule(Module module) {
        return moduleRepository.save(module);
    }

    // Méthode pour récupérer tous les modules
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    // Méthode pour récupérer un module par ID
    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }
    // Méthode pour mettre à jour un module
    public Module updateModule(Long id, Module updatedModule) {
        Module existingModule = moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module not found"));

        // Met à jour les informations du module
        existingModule.setName(updatedModule.getName());
        existingModule.setDescription(updatedModule.getDescription());

        return moduleRepository.save(existingModule);
    }

    // Méthode pour supprimer un module par ID
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

}

