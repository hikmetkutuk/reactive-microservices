package com.microservice.reactive.eventservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String eventName,
        @NotBlank LocalDateTime eventDate,
        @NotBlank String location,
        String description,
        String category
) {
}
