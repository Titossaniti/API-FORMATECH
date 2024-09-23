package com.example.apiformatech.repository;

import com.example.apiformatech.model.SessionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionUserRepository extends JpaRepository<SessionUser, Long> {
}
