package com.example.apiformatech.dto;

import java.util.Date;

public class SessionModuleDTO {
    private Long sessionId;
    private Long moduleId;
    private Long trainerId;
    private Date startDate;
    private Date endDate;

    public SessionModuleDTO(Long sessionId, Long moduleId, Long trainerId, Date startDate, Date endDate) {
        this.sessionId = sessionId;
        this.moduleId = moduleId;
        this.trainerId = trainerId;
        this.startDate = startDate;
        this.endDate = endDate;
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
