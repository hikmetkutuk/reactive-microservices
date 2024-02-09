package com.microservice.reactive.attendanceservice.dto;

import java.util.UUID;

public record EventResponse(
        UUID id,
        String eventName,
        String eventDate,
        String location,
        String description,
        String category
) {
}
