package com.example.apiformatech.repository;

import com.example.apiformatech.model.Establishment;
import com.example.apiformatech.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByName(String name);

    boolean existsByName(String name);

    boolean existsByEstablishmentAndStartDateBeforeAndEndDateAfter(
            Establishment establishment, Date endDate, Date startDate);

    List<Session> findByEstablishmentId(Long establishmentId);

    @Query("SELECT DISTINCT sm.session FROM SessionModule sm WHERE sm.trainer.id = :trainerId")
    List<Session> findByTrainerId(Long trainerId);
}