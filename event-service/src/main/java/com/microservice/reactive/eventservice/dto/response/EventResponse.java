package com.microservice.reactive.eventservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String eventName,
        LocalDateTime eventDate,
        String location,
        String description,
        String category,
        List<UserResponse> users
) {
}
