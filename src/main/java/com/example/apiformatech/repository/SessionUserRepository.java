package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionUser;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {

    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);

    Optional<SessionUser> findByUserIdAndSessionId(Long userId, Long sessionId);

    List<SessionUser> findByUser(User student);

    @Query("SELECT su.session FROM SessionUser su WHERE su.user = :student")
    List<Session> findSessionsByUser(User student);

    @Query("SELECT su.user FROM SessionUser su WHERE su.session.id = :sessionId")
    List<User> findUsersBySessionId(@Param("sessionId") Long sessionId);

}
