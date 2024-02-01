package com.microservice.reactive.attendanceservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AttendanceRequest(
        @NotBlank UUID eventId,
        @NotBlank UUID userId
) {
}
