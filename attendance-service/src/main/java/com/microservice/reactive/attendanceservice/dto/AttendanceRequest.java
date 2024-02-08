package com.microservice.reactive.attendanceservice.dto;

import java.util.UUID;

public record AttendanceRequest(
        UUID userId,
        UUID eventId
) {
}
