package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.SessionModuleRepository;
import com.example.apiformatech.repository.SessionRepository;
import com.example.apiformatech.repository.SessionUserRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionUserService {

    private final SessionUserRepository sessionUserRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionModuleRepository sessionModuleRepository;

    public SessionUserService(SessionUserRepository sessionUserRepository, UserRepository userRepository, SessionRepository sessionRepository, SessionModuleRepository sessionModuleRepository) {
        this.sessionUserRepository = sessionUserRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionModuleRepository = sessionModuleRepository;
    }

    // Ajouter un étudiant à une session
    public SessionUser addStudentToSession(Long userId, Long sessionId) {

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur avec l'ID " + userId + " n'existe pas."));
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("La session avec l'ID " + sessionId + " n'existe pas."));

        if (!student.getRole().getTitle().equals("STUDENT")) {
            throw new BadRequestException("Seuls les étudiants peuvent être ajoutés à une session.");
        }

        if (sessionUserRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new BadRequestException("Cet étudiant est déjà inscrit à cette session.");
        }

        SessionUser sessionUser = new SessionUser();
        sessionUser.setUser(student);
        sessionUser.setSession(session);

        return sessionUserRepository.save(sessionUser);
    }

    // Supprimer un étudiant d'une session
    public void removeStudentFromSession(Long userId, Long sessionId) {
        SessionUser sessionUser = sessionUserRepository.findByUserIdAndSessionId(userId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cet étudiant n'est pas inscrit à cette session."));

        sessionUserRepository.delete(sessionUser);
    }

    public List<User> getUsersBySession(Long sessionId, UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier que la session existe
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        // Si l'utilisateur est un superadmin, il peut voir tous les étudiants
        if (currentUser.getRole().getTitle().equals("SUPERADMIN")) {
            return sessionUserRepository.findUsersBySessionId(sessionId);
        }

        // Si l'utilisateur est un admin, il ne peut voir que les élèves de son établissement
        if (currentUser.getRole().getTitle().equals("ADMIN")) {
            if (!session.getEstablishment().equals(currentUser.getEstablishment())) {
                throw new BadRequestException("Vous ne pouvez voir que les utilisateurs de votre établissement.");
            }
            return sessionUserRepository.findUsersBySessionId(sessionId);
        }

        // Si l'utilisateur est un formateur, il ne peut voir que les élèves des sessions où il anime un module
        if (currentUser.getRole().getTitle().equals("TRAINER")) {
            boolean isTrainerInSession = sessionModuleRepository.existsByTrainerAndSession(currentUser, session);
            if (!isTrainerInSession) {
                throw new BadRequestException("Vous ne pouvez voir que les utilisateurs des sessions où vous enseignez.");
            }
            return sessionUserRepository.findUsersBySessionId(sessionId);
        }

        // Si l'utilisateur est un étudiant, il ne peut PAS voir les autres étudiants
        if (currentUser.getRole().getTitle().equals("STUDENT")) {
            throw new BadRequestException("Les étudiants ne peuvent pas voir la liste des autres étudiants.");
        }

        throw new BadRequestException("Accès refusé.");
    }

}
