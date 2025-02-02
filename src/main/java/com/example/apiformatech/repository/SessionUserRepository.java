package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {

    // Vérifier si un élève est déjà inscrit dans une session
    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);

    // Trouver une inscription d’un élève à une session
    Optional<SessionUser> findByUserIdAndSessionId(Long userId, Long sessionId);

    // Récupérer toutes les inscriptions d’un étudiant
    List<SessionUser> findByUser(User student);

    // Récupérer toutes les sessions d’un étudiant
//    @Query("SELECT su.session FROM SessionUser su WHERE su.user = :student")
    List<Session> findSessionsByUser(User student);
}
