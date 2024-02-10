package com.microservice.reactive.eventservice.service;

import com.microservice.reactive.eventservice.dto.request.EventRequest;
import com.microservice.reactive.eventservice.dto.response.EventResponse;
import com.microservice.reactive.eventservice.dto.response.UserResponse;
import com.microservice.reactive.eventservice.model.Event;
import com.microservice.reactive.eventservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    WebClient webClient = WebClient.create("http://localhost:8093/api/v1/attendance");

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Mono<ResponseEntity<?>> createEvent(EventRequest eventRequest) {
        Event newEvent = Event.builder()
                .id(UUID.randomUUID())
                .eventName(eventRequest.eventName())
                .eventDate(eventRequest.eventDate())
                .location(eventRequest.location())
                .description(eventRequest.description())
                .category(eventRequest.category())
                .createdDate(LocalDateTime.now())
                .build();

        return eventRepository.save(newEvent)
                .doOnSuccess(response -> log.info("Event created successfully with name: {}", eventRequest.eventName()))
                .doOnError(DataAccessException.class, e -> log.error("An error occurred while saving event to the database {}", e.getMessage()))
                .map(salon -> ResponseEntity.status(HttpStatus.CREATED).body(salon));
    }

    public Flux<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .flatMap(event -> webClient.get()
                        .uri("/user/list/{eventId}", event.getId())
                        .retrieve()
                        .bodyToFlux(UserResponse.class)
                        .collectList()
                        .map(userList -> {
                            if (userList.isEmpty()) {
                                return new EventResponse(event.getId(), event.getEventName(), event.getEventDate(), event.getLocation(), event.getDescription(), event.getCategory(), Collections.emptyList());
                            } else {
                                return mapToEventResponse(event, userList);
                            }
                        }))
                .doOnTerminate(() -> log.info("Event fetching process completed"))
                .onErrorResume(DataAccessException.class, e -> {
                    log.error("An error occurred while fetching events from the database", e);
                    return Flux.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("An unexpected error occurred while fetching events", e);
                    return Flux.empty();
                });
    }

    public Mono<EventResponse> getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .flatMap(event -> webClient.get()
                        .uri("/user/list/{eventId}", eventId)
                        .retrieve()
                        .bodyToFlux(UserResponse.class)
                        .collectList()
                        .map(userList -> mapToEventResponse(event, userList)))
                .doOnTerminate(() -> log.info("Event fetching by id process completed"))
                .onErrorResume(DataAccessException.class, e -> {
                    log.error("An error occurred while fetching event by id from the database {}", e.getMessage());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("An unexpected error occurred while fetching event by id", e);
                    return Mono.empty();
                });
    }

    private EventResponse mapToEventResponse(Event event, List<UserResponse> userList) {
        List<UserResponse> users = userList.stream()
                .map(userResponse ->
                        new UserResponse(
                                userResponse.id(),
                                userResponse.email(),
                                userResponse.firstname(),
                                userResponse.lastname(),
                                userResponse.roles()))
                .collect(Collectors.toList());

        return new EventResponse(event.getId(), event.getEventName(), event.getEventDate(), event.getLocation(), event.getDescription(), event.getCategory(), users);
    }
}
