package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.EstablishmentRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;

    // Injection des dépendances via le constructeur
    public EstablishmentService(EstablishmentRepository establishmentRepository, UserRepository userRepository) {
        this.establishmentRepository = establishmentRepository;
        this.userRepository = userRepository;
    }

    // Méthode pour sauvegarder un établissement
    public Establishment saveEstablishment(Establishment establishment) {
        if (establishmentRepository.existsByNameAndAddress(establishment.getName(), establishment.getAddress())) {
            throw new ResourceNotFoundException("Un établissement avec le même nom et la même adresse existe déjà.");
        }
        return establishmentRepository.save(establishment);
    }

    // assigner un admin à un établissement
    public User assignAdminToEstablishment(Long adminId, Long establishmentId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trouvé"));
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé"));

        if (!admin.getRole().getTitle().equals("ADMIN")) {
            throw new BadRequestException("Seuls les administrateurs peuvent être rattachés à un établissement.");
        }

        if (admin.getEstablishment() != null && !admin.getEstablishment().getId().equals(establishmentId)) {
            throw new BadRequestException("L'admin est déjà rattaché à un autre établissement.");
        }

        admin.setEstablishment(establishment);
        return userRepository.save(admin);
    }

    // Méthode pour récupérer les établissements en fonction du rôle
    public List<Establishment> getAllEstablishments(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Si l'utilisateur est SUPERADMIN, récupérer tous les établissements
        if (user.getRole().getTitle().equals("SUPERADMIN")) {
            return establishmentRepository.findAll();
        }

        // Si l'utilisateur est ADMIN, récupérer uniquement son établissement
        else if (user.getRole().getTitle().equals("ADMIN")) {
            if (user.getEstablishment() == null) {
                throw new BadRequestException("Aucun établissement associé à cet administrateur.");
            }
            return List.of(user.getEstablishment());
        }

        // Si l'utilisateur n'est ni SUPERADMIN ni ADMIN, accès interdit
        else {
            throw new BadRequestException("Accès interdit.");
        }
    }


    // Méthode pour récupérer un établissement par ID
    public Optional<Establishment> getEstablishmentById(Long id) {
        return establishmentRepository.findById(id);
    }

    // Méthode pour mettre à jour un établissement par ID
    public Establishment updateEstablishment(Long id, Establishment updatedEstablishment) {
        Establishment existingEstablishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establishment not found"));

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
        if (!establishmentRepository.existsById(id)){
            throw new ResourceNotFoundException("L'établissement avec l'ID" + id + " n'existe pas");
        }
        establishmentRepository.deleteById(id);
    }

}

