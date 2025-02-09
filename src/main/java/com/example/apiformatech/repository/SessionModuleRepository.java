package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.Module;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionModuleRepository extends JpaRepository<SessionModule, Long> {

    // Vérifier si un formateur est déjà assigné à un module pour une session
    boolean existsBySessionAndModuleAndTrainer(Session session, Module module, User trainer);

    // Récupérer tous les modules d'un étudiant en passant par ses sessions
    List<SessionModule> findBySessionIn(List<Session> sessions);

    List<SessionModule> findByTrainer(User trainer);

    boolean existsByTrainerAndModule_Id(User trainer, Long moduleId);

    boolean existsByTrainerAndSession(User currentUser, Session session);

    Optional<Object> findByModule_IdAndSession_Id(Long moduleId, Long sessionId);
}
