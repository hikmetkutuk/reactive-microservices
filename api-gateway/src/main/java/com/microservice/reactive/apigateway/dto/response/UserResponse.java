package com.microservice.reactive.apigateway.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String password,
        Boolean isActive,
        String firstname,
        String lastname,
        List<String> roles
) {
}
