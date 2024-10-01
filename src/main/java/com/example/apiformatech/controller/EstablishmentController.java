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

    /**
     * Crée un nouvel établissement.
     * @param establishment L'objet établissement à créer.
     * @return L'établissement créé.
     */
    @PostMapping
    public ResponseEntity<Establishment> createEstablishment(@RequestBody Establishment establishment) {
        Establishment savedEstablishment = establishmentService.saveEstablishment(establishment);
        return ResponseEntity.ok(savedEstablishment); // HTTP 200 avec l'établissement créé.
    }

//    * Récupère la liste de tous les établissements.
    @GetMapping
    public List<Establishment> getAllEstablishments() {
        return establishmentService.getAllEstablishments(); // Renvoie la liste de tous les établissements.
    }

    /**
     * Supprime un établissement par ID.
     * @param id L'ID de l'établissement à supprimer.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        establishmentService.deleteEstablishment(id); // Supprime l'établissement via le service.
        return ResponseEntity.noContent().build(); // HTTP 204 No Content pour indiquer que la suppression a été effectuée.
    }
}
