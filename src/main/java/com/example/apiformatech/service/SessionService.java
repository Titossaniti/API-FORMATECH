package com.example.apiformatech.service;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.repository.EstablishmentRepository;
import com.example.apiformatech.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final EstablishmentRepository establishmentRepository;

    // Injection des dépendances via le constructeur
    public SessionService(SessionRepository sessionRepository, EstablishmentRepository establishmentRepository) {
        this.sessionRepository = sessionRepository;
        this.establishmentRepository = establishmentRepository;
    }

    // Méthode pour sauvegarder une session
    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    // Méthode pour récupérer toutes les sessions
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    // Méthode pour récupérer une session par ID
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    // Méthode pour associer une session à un établissement
    public Session assignSessionToEstablishment(Long sessionId, Long establishmentId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        Establishment establishment = establishmentRepository.findById(establishmentId).orElseThrow(() -> new RuntimeException("Establishment not found"));
        session.setEstablishment(establishment);
        return sessionRepository.save(session);
    }
    // Méthode pour mettre à jour une session de formation
    public Session updateSession(Long id, Session updatedSession) {
        Session existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Met à jour les informations de la session
        existingSession.setType(updatedSession.getType());
        existingSession.setDescription(updatedSession.getDescription());
        existingSession.setStartDate(updatedSession.getStartDate());
        existingSession.setEndDate(updatedSession.getEndDate());
        existingSession.setEstablishment(updatedSession.getEstablishment());

        return sessionRepository.save(existingSession);
    }

    // Méthode pour supprimer une session
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

}

