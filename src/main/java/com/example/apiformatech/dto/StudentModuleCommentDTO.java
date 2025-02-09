package com.example.apiformatech.dto;

public class StudentModuleCommentDTO {
    private Long id;
    private Long studentId;
    private Double grade;
    private String comment;
    private Long moduleId;
    private Long sessionId;

    public StudentModuleCommentDTO(Long id, Long studentId, double grade, String comment, Long moduleId, Long sessionId) {
        this.id = id;
        this.studentId = studentId;
        this.grade = grade;
        this.comment = comment;
        this.moduleId = moduleId;
        this.sessionId = sessionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}
