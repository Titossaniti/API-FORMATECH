package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByName(String name);
    boolean existsByName(String name);
}
