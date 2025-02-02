package com.example.apiformatech.service;

import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.EstablishmentRepository;
import com.example.apiformatech.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final EstablishmentRepository establishmentRepository;

    public SessionService(SessionRepository sessionRepository, EstablishmentRepository establishmentRepository) {
        this.sessionRepository = sessionRepository;
        this.establishmentRepository = establishmentRepository;
    }

    // Créer une session en la liant à un établissement
    public Session createSession(Session session, Long establishmentId, User user) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé"));

        // Vérifier que l'utilisateur est autorisé à créer une session pour cet établissement
        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                (user.getEstablishment() == null || !user.getEstablishment().getId().equals(establishmentId))) {
            throw new RuntimeException("Vous ne pouvez créer une session que pour votre établissement.");
        }

        session.setEstablishment(establishment);
        return sessionRepository.save(session);
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
    public Session updateSession(Long id, Session updatedSession, User user) {
        Session existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                (user.getEstablishment() == null || !user.getEstablishment().equals(existingSession.getEstablishment()))) {
            throw new RuntimeException("Vous ne pouvez modifier que les sessions de votre établissement.");
        }

        existingSession.setName(updatedSession.getName());
        existingSession.setDescription(updatedSession.getDescription());
        existingSession.setStartDate(updatedSession.getStartDate());
        existingSession.setEndDate(updatedSession.getEndDate());

        return sessionRepository.save(existingSession);
    }

    // Supprimer une session (Admin = uniquement ses sessions)
    public void deleteSession(Long id, User user) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        if (!user.getRole().getTitle().equals("SUPERADMIN") &&
                (user.getEstablishment() == null || !user.getEstablishment().equals(session.getEstablishment()))) {
            throw new RuntimeException("Vous ne pouvez supprimer que les sessions de votre établissement.");
        }

        sessionRepository.deleteById(id);
    }
}
