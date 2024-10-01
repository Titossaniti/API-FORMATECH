package com.example.apiformatech.controller;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.service.EstablishmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    public EstablishmentController(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;
    }

    // Créer ou mettre à jour un établissement
    @PostMapping
    public ResponseEntity<Establishment> createOrUpdateEstablishment(@RequestBody Establishment establishment) {
        Establishment savedEstablishment = establishmentService.saveEstablishment(establishment);
        return ResponseEntity.ok(savedEstablishment);
    }

    // Récupérer tous les établissements
    @GetMapping
    public ResponseEntity<List<Establishment>> getAllEstablishments() {
        return ResponseEntity.ok(establishmentService.getAllEstablishments());
    }

    // Récupérer un établissement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Establishment> getEstablishmentById(@PathVariable Long id) {
        return establishmentService.getEstablishmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour un établissement
    @PutMapping("/{id}")
    public ResponseEntity<Establishment> updateEstablishment(@PathVariable Long id, @RequestBody Establishment establishment) {
        Establishment updatedEstablishment = establishmentService.updateEstablishment(id, establishment);
        return ResponseEntity.ok(updatedEstablishment);
    }

    // Supprimer un établissement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        establishmentService.deleteEstablishment(id);
        return ResponseEntity.noContent().build();
    }
}
