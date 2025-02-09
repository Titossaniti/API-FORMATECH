package com.example.apiformatech.repository;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentModuleCommentRepository extends JpaRepository<StudentModuleComment, Long> {

    List<StudentModuleComment> findByStudentAndSessionModule(User student, SessionModule sessionModule);

    List<StudentModuleComment> findByTrainerAndSessionModule(User trainer, SessionModule sessionModule);

    @Query("SELECT smc FROM StudentModuleComment smc WHERE smc.sessionModule.module.id = :moduleId " +
            "AND smc.sessionModule.session.id = :sessionId " +
            "AND smc.sessionModule.session.establishment = :establishment")
    List<StudentModuleComment> findByEstablishment(Long moduleId, Long sessionId, Establishment establishment);

    @Query("SELECT smc FROM StudentModuleComment smc WHERE smc.sessionModule.module.id = :moduleId " +
            "AND smc.sessionModule.session.id = :sessionId")
    List<StudentModuleComment> findByModuleAndSession(Long moduleId, Long sessionId);

    @Query("SELECT smc FROM StudentModuleComment smc " +
            "WHERE smc.sessionModule.session.id = :sessionId " +
            "AND smc.sessionModule.module.id IN (" +
            "   SELECT sm.module.id FROM SessionModule sm " +
            "   WHERE sm.session.id = :sessionId " +
            "   AND sm.trainer = :trainer)")

    List<StudentModuleComment> findBySessionForTrainer(@Param("sessionId") Long sessionId, @Param("trainer") User trainer);
}
