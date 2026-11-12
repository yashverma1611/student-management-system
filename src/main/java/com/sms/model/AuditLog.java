package com.sms.model;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private Timestamp timestamp;
    private String operator;
    private String actionType;
    private String targetEntity;
    private String details;

    public AuditLog() {}

    public AuditLog(String operator, String actionType, String targetEntity, String details) {
        this.operator = operator;
        this.actionType = actionType;
        this.targetEntity = targetEntity;
        this.details = details;
    }

    public AuditLog(int id, Timestamp timestamp, String operator, String actionType, String targetEntity, String details) {
        this.id = id;
        this.timestamp = timestamp;
        this.operator = operator;
        this.actionType = actionType;
        this.targetEntity = targetEntity;
        this.details = details;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTargetEntity() { return targetEntity; }
    public void setTargetEntity(String targetEntity) { this.targetEntity = targetEntity; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
