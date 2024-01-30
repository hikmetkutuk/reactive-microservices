package com.microservice.reactive.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserRequest(
        @NotBlank String email,
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank String password,
        List<String> roles
) {
}
