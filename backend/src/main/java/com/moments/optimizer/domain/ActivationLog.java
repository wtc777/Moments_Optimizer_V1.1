package com.moments.optimizer.domain;

import java.time.LocalDateTime;

public class ActivationLog {

    private String id;
    private String userId;
    private String codeId;
    private Integer addedUses;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public Integer getAddedUses() {
        return addedUses;
    }

    public void setAddedUses(Integer addedUses) {
        this.addedUses = addedUses;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
