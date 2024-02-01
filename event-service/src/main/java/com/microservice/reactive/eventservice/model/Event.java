package com.microservice.reactive.eventservice.model;

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
public class Event {
    @Id
    private UUID id;

    private String eventName;

    private LocalDateTime eventDate;

    private String location;

    private String description;

    private String category;

    @Builder.Default()
    private boolean active = true;

    @Builder.Default()
    private boolean deleted = false;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
