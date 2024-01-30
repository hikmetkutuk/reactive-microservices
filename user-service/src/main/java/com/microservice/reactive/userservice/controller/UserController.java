package com.microservice.reactive.userservice.controller;

import com.microservice.reactive.userservice.dto.request.AuthRequest;
import com.microservice.reactive.userservice.dto.request.UserRequest;
import com.microservice.reactive.userservice.dto.response.UserResponse;
import com.microservice.reactive.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> register(@Valid @RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> register(@Valid @RequestBody AuthRequest authRequest) {
        return userService.login(authRequest);
    }

    @GetMapping("/list")
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/list/{id}")
    public Mono<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable UUID id) {
        return userService.softDeleteUser(id);
    }
}
