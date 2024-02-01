package com.microservice.reactive.eventservice.service;

import com.microservice.reactive.eventservice.dto.request.EventRequest;
import com.microservice.reactive.eventservice.model.Event;
import com.microservice.reactive.eventservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

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
                .build();

        return eventRepository.save(newEvent)
                .doOnSuccess(response -> log.info("Event created successfully with name: {}", eventRequest.eventName()))
                .doOnError(DataAccessException.class, e -> log.error("An error occurred while saving event to the database {}", e.getMessage()))
                .map(salon -> ResponseEntity.status(HttpStatus.CREATED).body(salon));
    }
}
