package com.microservice.reactive.userservice.handler;

import com.microservice.reactive.userservice.dto.request.AuthRequest;
import com.microservice.reactive.userservice.dto.request.UserRequest;
import com.microservice.reactive.userservice.dto.response.UserResponse;
import com.microservice.reactive.userservice.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> handleRegister(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(UserRequest.class)
                .flatMap(userService::register)
                .flatMap(registeredUser -> ServerResponse.ok().bodyValue(registeredUser))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> handleLogin(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(AuthRequest.class)
                .flatMap(userService::login)
                .flatMap(registeredUser -> ServerResponse.ok().bodyValue(registeredUser))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> handleGetAllUsers(ServerRequest request) {
        Flux<UserResponse> users = userService.getAllUsers();
        return ServerResponse.ok().body(users, UserResponse.class);
    }

    public Mono<ServerResponse> handleGetUserById(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("id"));
        Mono<UserResponse> user = userService.getUserById(userId);
        return ServerResponse.ok().body(user, UserResponse.class);
    }

    public Mono<ServerResponse> handleUpdateUser(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("id"));

        return request
                .bodyToMono(UserRequest.class)
                .flatMap(userRequest ->
                        userService.updateUser(userId, userRequest))
                .flatMap(updatedUser ->
                        ServerResponse.ok().bodyValue(updatedUser))
                .onErrorResume(error ->
                        ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> handleDeleteUser(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("id"));

        return userService.softDeleteUser(userId)
                .flatMap(success -> ServerResponse.ok().bodyValue(success))
                .onErrorResume(error ->
                        ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

}
