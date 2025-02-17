package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.Module;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Récupérer les sessions/modules d'un établissement
    @Query("SELECT sm FROM SessionModule sm WHERE sm.session.establishment.id = :establishmentId")
    List<SessionModule> findBySession_EstablishmentId(Long establishmentId);

    // Récupérer les sessions/modules d'un formateur
    @Query("SELECT sm FROM SessionModule sm WHERE sm.trainer.id = :trainerId")
    List<SessionModule> findByTrainerId(Long trainerId);

    // Récupérer les sessions/modules où un étudiant est inscrit
    @Query("SELECT sm FROM SessionModule sm WHERE sm.session.id IN " +
            "(SELECT su.session.id FROM SessionUser su WHERE su.user.id = :studentId)")
    List<SessionModule> findBySession_StudentsId(Long studentId);

    @Query("SELECT sm FROM SessionModule sm WHERE sm.session.id = :sessionId")
    List<SessionModule> findBySession_Id(@Param("sessionId") Long sessionId);

}
