package com.microservice.reactive.userservice.service;

import com.microservice.reactive.userservice.dto.request.AuthRequest;
import com.microservice.reactive.userservice.dto.request.UserRequest;
import com.microservice.reactive.userservice.dto.response.EventResponse;
import com.microservice.reactive.userservice.dto.response.UserResponse;
import com.microservice.reactive.userservice.exception.ResourceNotFoundException;
import com.microservice.reactive.userservice.model.User;
import com.microservice.reactive.userservice.repository.UserRepository;
import com.microservice.reactive.userservice.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    WebClient webClient = WebClient.create("http://localhost:8093/api/v1/attendance");

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, ReactiveAuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public Mono<ResponseEntity<?>> register(UserRequest userRequest) {
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .email(userRequest.email())
                .firstname(userRequest.firstname())
                .lastname(userRequest.lastname())
                .password(passwordEncoder.encode(userRequest.password()))
                .roles(userRequest.roles())
                .build();

        return userRepository.save(newUser)
                .flatMap(savedUser -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            savedUser.getEmail(), savedUser.getPassword(),
                            AuthorityUtils.createAuthorityList(savedUser.getRoles().toArray(new String[0])));
                    return createJwtResponse(authentication)
                            .doOnSuccess(success -> log.info("User {} registered successfully", userRequest.email()))
                            .publishOn(Schedulers.boundedElastic())
                            .doOnError(error -> {
                                userRepository.deleteById(savedUser.getId()).subscribe();
                                log.error("An error occurred during registration {}", error.getMessage());
                            });
                })
                .doOnError(DataAccessException.class, e -> log.error("An error occurred while saving user to the database {}", e.getMessage()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Map.of("error", "An error occurred during registration " + e.getMessage()))));
    }

    public Mono<ResponseEntity<?>> login(AuthRequest authRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequest.email(), authRequest.password());

        return authenticationManager.authenticate(authentication)
                .flatMap(this::createJwtResponse)
                .doOnSuccess(response -> log.info("User {} logged in successfully", authRequest.email()))
                .onErrorResume(AuthenticationException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Map.of("error", "Invalid credentials"))));
    }

    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAllByDeletedFalse()
                .flatMap(user -> webClient.get()
                        .uri("/event/list/{userId}", user.getId())
                        .retrieve()
                        .bodyToFlux(EventResponse.class)
                        .collectList()
                        .map(eventList -> {
                            if (eventList.isEmpty()) {
                                return new UserResponse(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname(), user.getRoles(), Collections.emptyList());
                            } else {
                                return mapToUserResponse(user, eventList);
                            }
                        }))
                .doOnTerminate(() -> log.info("User fetching process completed"))
                .onErrorResume(DataAccessException.class, e -> {
                    log.error("An error occurred while fetching users from the database", e);
                    return Flux.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("An unexpected error occurred while fetching users", e);
                    return Flux.empty();
                });
    }

    public Mono<UserResponse> getUserById(UUID userId) {
        return userRepository.findByIdWithLimit(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with ID: " + userId)))
                .flatMap(user -> webClient.get()
                        .uri("/event/list/{userId}", userId)
                        .retrieve()
                        .bodyToFlux(EventResponse.class)
                        .collectList()
                        .map(eventList -> mapToUserResponse(user, eventList)))
                .doOnSuccess(success -> log.info("User fetched for user with ID: {}", userId))
                .doOnError(error -> log.error("An error occurred during user details fetching for user with ID: {} {}", userId, error.getMessage()));
    }

    public Mono<ResponseEntity<UserResponse>> updateUser(UUID userId, UserRequest userRequest) {
        return userRepository.findByIdWithLimit(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with ID: " + userId)))
                .flatMap(user -> {
                    user.setFirstname(userRequest.firstname());
                    user.setLastname(userRequest.lastname());
                    user.setRoles(userRequest.roles());
                    user.setUpdatedDate(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .map(updatedUser -> ResponseEntity.ok().body(new UserResponse(updatedUser.getId(), updatedUser.getEmail(), updatedUser.getFirstname(), updatedUser.getLastname(), updatedUser.getRoles(), null)))
                .doOnSuccess(success -> log.info("User updated for user with ID: {}", userId))
                .doOnError(error -> log.error("An error occurred during user details update for user with ID: {}", userId, error));
    }

    private Mono<ResponseEntity<?>> createJwtResponse(Authentication authentication) {
        String jwt = jwtTokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        Map<String, Object> tokenBody = Map.of("access_token", jwt);
        return Mono.just(ResponseEntity.ok().headers(httpHeaders).body(tokenBody));
    }

    public Mono<ResponseEntity<String>> softDeleteUser(UUID userId) {
        return userRepository.findByIdWithLimit(userId)
                .flatMap(user -> {
                    user.setDeleted(true);
                    return userRepository.save(user)
                            .map(updatedUser -> ResponseEntity.ok().body("User deleted successfully"))
                            .doOnSuccess(success -> log.info("User deleted for user with ID: {}", userId))
                            .doOnError(error -> log.error("An error occurred during user delete for user with ID: {} {}", userId, error.getMessage()));
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with ID: " + userId)))
                .onErrorResume(DataAccessException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())))
                .onErrorResume(Exception.class, e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    private UserResponse mapToUserResponse(User user, List<EventResponse> eventList) {
        List<EventResponse> events = eventList.stream()
                .map(eventResponse ->
                        new EventResponse(
                                eventResponse.id(),
                                eventResponse.eventName(),
                                eventResponse.eventDate(),
                                eventResponse.location(),
                                eventResponse.description(),
                                eventResponse.category()))
                .collect(Collectors.toList());

        return new UserResponse(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname(), user.getRoles(), events);
    }
}
