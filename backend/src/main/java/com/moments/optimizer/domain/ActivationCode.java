package com.moments.optimizer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "activation_codes")
public class ActivationCode {

    @Id
    @Column(length = 64)
    private String id;

    @Column(length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "batch_id", length = 64)
    private String batchId;

    @Column(name = "total_uses", nullable = false)
    private Integer totalUses;

    @Column(name = "used_uses", nullable = false)
    private Integer usedUses = 0;

    @Column(length = 32, nullable = false)
    private String status = "unused";

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Integer getTotalUses() {
        return totalUses;
    }

    public void setTotalUses(Integer totalUses) {
        this.totalUses = totalUses;
    }

    public Integer getUsedUses() {
        return usedUses;
    }

    public void setUsedUses(Integer usedUses) {
        this.usedUses = usedUses;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
