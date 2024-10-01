package com.example.apiformatech.service;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.repository.EstablishmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;

    // Injection des dépendances via le constructeur
    public EstablishmentService(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    // Méthode pour sauvegarder un établissement
    public Establishment saveEstablishment(Establishment establishment) {
        return establishmentRepository.save(establishment);
    }

    // Méthode pour récupérer tous les établissements
    public List<Establishment> getAllEstablishments() {
        return establishmentRepository.findAll();
    }

    // Méthode pour récupérer un établissement par ID
    public Optional<Establishment> getEstablishmentById(Long id) {
        return establishmentRepository.findById(id);
    }

    // Méthode pour mettre à jour un établissement par ID
    public Establishment updateEstablishment(Long id, Establishment updatedEstablishment) {
        Establishment existingEstablishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));

        // Met à jour les informations de l'établissement
        existingEstablishment.setName(updatedEstablishment.getName());
        existingEstablishment.setCity(updatedEstablishment.getCity());
        existingEstablishment.setPostalCode(updatedEstablishment.getPostalCode());
        existingEstablishment.setAddress(updatedEstablishment.getAddress());
        existingEstablishment.setPhone(updatedEstablishment.getPhone());
        existingEstablishment.setEmail(updatedEstablishment.getEmail());

        return establishmentRepository.save(existingEstablishment);
    }


    // Méthode pour supprimer un établissement par ID
    public void deleteEstablishment(Long id) {
        establishmentRepository.deleteById(id);
    }

}

