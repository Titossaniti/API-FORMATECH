package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.*;
import com.example.apiformatech.repository.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;
    private final SessionModuleRepository sessionModuleRepository;
    private final SessionUserRepository sessionUserRepository;

    public SessionService(SessionRepository sessionRepository,
                          EstablishmentRepository establishmentRepository,
                          UserRepository userRepository,
                          SessionModuleRepository sessionModuleRepository,
                          SessionUserRepository sessionUserRepository) {
        this.sessionRepository = sessionRepository;
        this.establishmentRepository = establishmentRepository;
        this.userRepository = userRepository;
        this.sessionModuleRepository = sessionModuleRepository;
        this.sessionUserRepository = sessionUserRepository;
    }


    // Créer une session en la liant à un établissement
    @Transactional
    public Session createSession(Session session, Long establishmentId, User user) {
        // Récupérer l'admin avec son établissement chargé
        User admin = userRepository.findByEmailWithEstablishment(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé"));

        // Vérifier si l'admin est bien rattaché à cet établissement
        if (!admin.getRole().getTitle().equals("SUPERADMIN") &&
                (admin.getEstablishment() == null || !admin.getEstablishment().getId().equals(establishmentId))) {
            throw new BadRequestException("Vous ne pouvez créer une session que dans votre établissement.");
        }

        session.setEstablishment(establishment);
        return sessionRepository.save(session);
    }

    // Ajouter un étudiant à une session
    @Transactional
    public SessionUser addStudentToSession(Long sessionId, Long studentId, User user) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));

        // Seuls les admins de l'établissement ou superadmins peuvent inscrire un élève
        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                !user.getEstablishment().equals(session.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez inscrire des étudiants que dans votre établissement.");
        }

        //  Vérifier si l'étudiant est déjà inscrit
        if (sessionUserRepository.existsByUserIdAndSessionId(studentId, sessionId)) {
            throw new BadRequestException("Cet élève est déjà inscrit à cette session.");
        }

        SessionUser sessionUser = new SessionUser();
        sessionUser.setSession(session);
        sessionUser.setUser(student);
        return sessionUserRepository.save(sessionUser);
    }


    // voir les modules d'un élève
    public List<SessionModule> getStudentModules(UserDetails userDetails) {
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Récupérer toutes les sessions associées
        List<Session> studentSessions = sessionUserRepository.findByUser(student).stream()
                .map(SessionUser::getSession)
                .toList();

        return sessionModuleRepository.findBySessionIn(studentSessions);
    }


    // Récupérer toutes les sessions
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }


    // Récupérer une session par ID
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    // Mettre à jour une session (Admin = uniquement ses sessions)
    @Transactional
    public Session updateSession(Long id, Session updatedSession, User user) {
        Session existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                !user.getEstablishment().equals(existingSession.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez modifier que les sessions de votre établissement.");
        }

        existingSession.setName(updatedSession.getName());
        existingSession.setDescription(updatedSession.getDescription());
        existingSession.setStartDate(updatedSession.getStartDate());
        existingSession.setEndDate(updatedSession.getEndDate());

        return sessionRepository.save(existingSession);
    }

    // Récupérer les sessions d'un établissement
    public List<Session> getSessionsByEstablishment(Long establishmentId) {
        return sessionRepository.findByEstablishmentId(establishmentId);
    }

    // Récupérer les sessions d'un formateur via les modules qu'il anime
    public List<Session> getTrainerSessions(Long trainerId) {
        return sessionRepository.findByTrainerId(trainerId);
    }

    // Récupérer les sessions d'un élève
    public List<Session> getStudentSessions(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BadRequestException("Élève introuvable"));

        return sessionUserRepository.findSessionsByUser(student);
    }

    // Supprimer une session (Admin = uniquement ses sessions)
    @Transactional
    public void deleteSession(Long id, User user) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                !user.getEstablishment().equals(session.getEstablishment())) {
            throw new BadRequestException("Vous ne pouvez supprimer que les sessions de votre établissement.");
        }

        // Vérifier s’il y a des étudiants dans la session
        if (!sessionUserRepository.findByUser(user).isEmpty()) {
            throw new BadRequestException("Impossible de supprimer une session contenant des étudiants.");
        }

        // Vérifier s’il y a des modules associés à la session
        if (!sessionModuleRepository.findBySessionIn(List.of(session)).isEmpty()) {
            throw new BadRequestException("Impossible de supprimer une session contenant des modules.");
        }

        sessionRepository.deleteById(id);
    }
}
