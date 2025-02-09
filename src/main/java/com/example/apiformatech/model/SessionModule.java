package com.example.apiformatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.util.Date;


@Entity
@Table(name = "session_modules",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"module_id", "session_id"})
        })
public class SessionModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La session est obligatoire")
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @NotNull(message = "Le module est obligatoire")
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @NotNull(message = "Le formateur est obligatoire")
    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être dans le futur ou le présent")
    private Date startDate;

    @Future(message = "La date de fin doit être dans le futur")
    private Date endDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

