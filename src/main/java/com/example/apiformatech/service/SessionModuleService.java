package com.example.apiformatech.service;

import com.example.apiformatech.dto.SessionModuleDTO;
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
import java.util.stream.Collectors;

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

    // Récupérer toutes les sessions/modules selon le role de l'user
    public List<SessionModuleDTO> getAllSessionModules(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        List<SessionModule> sessionModules;

        if (user.getRole().getTitle().equals("SUPERADMIN")) {
            // SuperAdmin voit tout
            sessionModules = sessionModuleRepository.findAll();
        } else if (user.getRole().getTitle().equals("ADMIN")) {
            // Admin voit uniquement les sessions/modules de son établissement
            if (user.getEstablishment() == null) {
                throw new BadRequestException("Vous n'êtes pas rattaché à un établissement.");
            }
            sessionModules = sessionModuleRepository.findBySession_EstablishmentId(user.getEstablishment().getId());
        } else if (user.getRole().getTitle().equals("TRAINER")) {
            // Formateur voit uniquement les sessions/modules où il est assigné
            sessionModules = sessionModuleRepository.findByTrainerId(user.getId());
        } else if (user.getRole().getTitle().equals("STUDENT")) {
            // Élève voit uniquement les sessions/modules liés aux sessions où il est inscrit
            sessionModules = sessionModuleRepository.findBySession_StudentsId(user.getId());
        } else {
            throw new BadRequestException("Accès interdit.");
        }

        // Transformer les entités en DTOs
        return sessionModules.stream()
                .map(sm -> new SessionModuleDTO(
                        sm.getSession().getId(),
                        sm.getModule().getId(),
                        sm.getTrainer().getId(),
                        sm.getStartDate(),
                        sm.getEndDate()
                ))
                .collect(Collectors.toList());
    }

    // Récupérer tous les modules d'un formateur
    public List<Module> getModulesByTrainer(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));

        List<SessionModule> sessionModules = sessionModuleRepository.findByTrainer(trainer);

        if (sessionModules.isEmpty()) {
            throw new ResourceNotFoundException("Aucun module trouvé pour ce formateur.");
        }

        return sessionModuleRepository.findByTrainer(trainer)
                .stream()
                .map(SessionModule::getModule)
                .distinct() // Pour éviter les doublons
                .toList();
    }


    // Méthode pour supprimer une relation session-module
    public void deleteSessionModule(Long id) {
        if (!sessionModuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("SessionModule avec l'ID " + id + " n'existe pas");
        }
        sessionModuleRepository.deleteById(id);
    }

    public List<Session> getSessionsByTrainer(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));

        return sessionModuleRepository.findByTrainer(trainer)
                .stream()
                .map(SessionModule::getSession)
                .distinct()
                .toList();
    }

    // Récupérer tous les modules liés à une session
    public List<Module> getModulesBySession(Long sessionId) {
        List<SessionModule> sessionModules = sessionModuleRepository.findBySession_Id(sessionId);

        if (sessionModules.isEmpty()) {
            throw new ResourceNotFoundException("Aucun module trouvé pour cette session.");
        }

        return sessionModules.stream()
                .map(SessionModule::getModule) // On extrait uniquement les modules
                .collect(Collectors.toList());
    }

}

