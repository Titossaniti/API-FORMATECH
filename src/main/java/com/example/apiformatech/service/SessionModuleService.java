package com.example.apiformatech.service;

import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.model.Module;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.ModuleRepository;
import com.example.apiformatech.repository.SessionModuleRepository;
import com.example.apiformatech.repository.SessionRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SessionModuleService {

    private final SessionModuleRepository sessionModuleRepository;
    private final SessionRepository sessionRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    // Injection des dépendances via le constructeur
    public SessionModuleService(SessionModuleRepository sessionModuleRepository, SessionRepository sessionRepository, ModuleRepository moduleRepository, UserRepository userRepository) {
        this.sessionModuleRepository = sessionModuleRepository;
        this.sessionRepository = sessionRepository;
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
    }

    // Méthode pour associer un module et un formateur à une session
    public SessionModule assignModuleToSession(Long sessionId, Long moduleId, Long trainerId, Date startDate, Date endDate, UserDetails userDetails) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("La session avec l'ID " + sessionId + "n'existe pas."));
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Le module avec l'ID " + moduleId + "n'existe pas."));
        User trainer = userRepository.findById(trainerId).orElseThrow(() -> new ResourceNotFoundException("L'enseignant avec l'ID " + trainerId + "n'existe pas."));
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        // Vérifier que l’admin n’essaie pas de gérer une session hors de son établissement
        if (currentUser.getRole().getTitle().equals("ADMIN") && !currentUser.getEstablishment().equals(session.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez gérer que les sessions de votre établissement");
        }
        // Vérifier s'il s'agit bien d'un formateur
        if (!trainer.getRole().getTitle().equals("TRAINER")) {
            throw new BadRequestException("L'utilisateur spécifié n'est pas un formateur.");
        }

        // Vérifier si ce formateur est déjà assigné à ce module sur cette session
        if (sessionModuleRepository.existsBySessionAndModuleAndTrainer(session, module, trainer)) {
            throw new BadRequestException("Ce formateur est déjà assigné à ce module pour cette session.");
        }

        // Vérifier que startDate < endDate
        if (startDate.after(endDate)) {
            throw new BadRequestException("La date de début doit être avant la date de fin.");
        }

        SessionModule sessionModule = new SessionModule();
        sessionModule.setSession(session);
        sessionModule.setModule(module);
        sessionModule.setTrainer(trainer);
        sessionModule.setStartDate(startDate);
        sessionModule.setEndDate(endDate);

        return sessionModuleRepository.save(sessionModule);
    }

    // Méthode pour récupérer tous les session-modules
    public List<SessionModule> getAllSessionModules() {
        return sessionModuleRepository.findAll();
    }

    // Méthode pour supprimer une relation session-module
    public void deleteSessionModule(Long id) {
        if (!sessionModuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("SessionModule avec l'ID " + id + " n'existe pas");
        }
        sessionModuleRepository.deleteById(id);
    }
}

