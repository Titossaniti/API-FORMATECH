package com.example.apiformatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "student_module_comments")
public class StudentModuleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'étudiant est obligatoire")
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull(message = "Le formateur est obligatoire")
    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @NotNull(message = "Le module de session est obligatoire")
    @ManyToOne
    @JoinColumn(name = "session_module_id", nullable = false)
    private SessionModule sessionModule;

    @Min(value = 0, message = "La note doit être au moins 0")
    @Max(value = 20, message = "La note ne peut pas dépasser 20")
    private int grade;

    @Size(max = 255, message = "Le commentaire ne peut pas dépasser 255 caractères")
    private String comment;

    @PastOrPresent(message = "La date de création doit être dans le passé ou le présent")
    private Date createdAt;

    @PastOrPresent(message = "La date de mise à jour doit être dans le passé ou le présent")
    private Date updatedAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getStudent() {
        return student;
    }
    public void setStudent(User student) {
        this.student = student;
    }
    public User getTrainer() {
        return trainer;
    }
    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }
    public SessionModule getSessionModule() {
        return sessionModule;
    }
    public void setSessionModule(SessionModule sessionModule) {
        this.sessionModule = sessionModule;
    }
    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

