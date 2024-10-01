package com.example.apiformatech.controller;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.service.EstablishmentService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Establishment> createEstablishment(@RequestBody Establishment establishment) {
        Establishment savedEstablishment = establishmentService.saveEstablishment(establishment);
        if (establishment.getId() == null) {
            // Si l'ID est null, c'est une création
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEstablishment);
        } else {
            // Sinon, c'est une mise à jour
            return ResponseEntity.ok(savedEstablishment);
        }
    }

    // Récupérer tous les établissements
    @GetMapping
    public ResponseEntity<List<Establishment>> getAllEstablishments() {
        List<Establishment> establishments = establishmentService.getAllEstablishments();
        if (establishments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(establishments);
    }


    // Récupérer un établissement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Establishment> getEstablishmentById(@PathVariable Long id) {
        return establishmentService.getEstablishmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Mettre à jour un établissement
    @PutMapping("/{id}")
    public ResponseEntity<Establishment> updateEstablishment(@PathVariable Long id, @RequestBody Establishment establishment) {
        try {
            Establishment updatedEstablishment = establishmentService.updateEstablishment(id, establishment);
            return ResponseEntity.ok(updatedEstablishment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Supprimer un établissement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        try {
            establishmentService.deleteEstablishment(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
