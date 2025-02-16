package com.example.apiformatech.dto;

public class SessionModuleDTO {
    private Long sessionId;
    private Long moduleId;
    private Long trainerId;

    public SessionModuleDTO(Long sessionId, Long moduleId, Long trainerId) {
        this.sessionId = sessionId;
        this.moduleId = moduleId;
        this.trainerId = trainerId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }
}
