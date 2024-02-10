package com.microservice.reactive.eventservice.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstname,
        String lastname,
        List<String> roles
) {
}
