package com.microservice.reactive.attendanceservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("attendance")
public class Attendance implements Serializable, Persistable<UUID> {

    @Id
    private UUID id;
    private UUID userId;
    private UUID eventId;
    @Builder.Default()
    private boolean active = true;
    @Builder.Default()
    private boolean deleted = false;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Transient
    private boolean isUpdated = false;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return !this.isUpdated || id == null;
    }
}
