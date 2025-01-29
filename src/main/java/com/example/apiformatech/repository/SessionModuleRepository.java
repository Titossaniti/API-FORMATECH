package com.example.apiformatech.repository;

import com.example.apiformatech.model.Session;
import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.Module;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionModuleRepository extends JpaRepository<SessionModule, Long> {
    boolean existsBySessionAndModuleAndTrainer(Session session, Module module, User trainer);
}
