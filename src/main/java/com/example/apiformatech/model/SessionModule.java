package com.example.apiformatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "session_modules")
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
}

