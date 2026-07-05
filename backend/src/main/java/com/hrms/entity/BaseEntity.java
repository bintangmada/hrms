package com.hrms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

// ==============================================================================
// BASE ENTITY (AUDIT TRAIL & SOFT DELETE)
// ==============================================================================
// MappedSuperclass to inherit common audit fields in all other entities.
// Contains createdBy, updatedBy, deletedBy, dates, status, and deleteStatus.
// ==============================================================================

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "status")
    private Integer status = 1; // 1 = Active, 0 = Inactive

    @Column(name = "deleted_status")
    private Integer deletedStatus = 0; // 0 = Active, 1 = Soft-deleted

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.deletedStatus == null) {
            this.deletedStatus = 0;
        }
        if (this.status == null) {
            this.status = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
