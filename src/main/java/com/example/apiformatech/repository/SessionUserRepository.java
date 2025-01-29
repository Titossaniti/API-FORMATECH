package com.example.apiformatech.repository;

import com.example.apiformatech.model.SessionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {
    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);
    Optional<SessionUser> findByUserIdAndSessionId(Long userId, Long sessionId);
}