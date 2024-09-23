package com.example.apiformatech.repository;

import com.example.apiformatech.model.SessionModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionModuleRepository extends JpaRepository<SessionModule, Long> {
}

