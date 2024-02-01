package com.microservice.reactive.attendanceservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Attendance {
    @Id
    private UUID id;

    private UUID eventId;

    private UUID userId;

    private LocalDateTime attendanceDate;

    @Builder.Default()
    private boolean active = true;

    @Builder.Default()
    private boolean deleted = false;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
