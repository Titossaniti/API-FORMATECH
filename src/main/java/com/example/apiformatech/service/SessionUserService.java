package com.example.apiformatech.service;

import com.example.apiformatech.exception.BadRequestException;
import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.model.User;
import com.example.apiformatech.repository.SessionRepository;
import com.example.apiformatech.repository.SessionUserRepository;
import com.example.apiformatech.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionUserService {

    private final SessionUserRepository sessionUserRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public SessionUserService(SessionUserRepository sessionUserRepository, UserRepository userRepository, SessionRepository sessionRepository) {
        this.sessionUserRepository = sessionUserRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    // Ajouter un étudiant à une session
    public SessionUser addStudentToSession(Long userId, Long sessionId) {
        if (sessionUserRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new BadRequestException("Cet étudiant est déjà inscrit à cette session.");
        }

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur avec l'ID " + userId + " n'existe pas."));
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("La session avec l'ID " + sessionId + " n'existe pas."));

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
}
