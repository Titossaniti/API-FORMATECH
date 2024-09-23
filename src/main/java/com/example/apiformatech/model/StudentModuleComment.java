package com.example.apiformatech.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "student_module_comments")
public class StudentModuleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "session_module_id", nullable = false)
    private SessionModule sessionModule;

    private int grade;
    private String comment;
    private Date createdAt;
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

