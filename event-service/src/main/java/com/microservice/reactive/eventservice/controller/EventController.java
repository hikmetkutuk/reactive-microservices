package com.microservice.reactive.eventservice.controller;

import com.microservice.reactive.eventservice.dto.request.EventRequest;
import com.microservice.reactive.eventservice.dto.response.EventResponse;
import com.microservice.reactive.eventservice.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<?>> createEvent(@RequestBody EventRequest eventRequest) {
        return eventService.createEvent(eventRequest);
    }

    @GetMapping("/list")
    public Flux<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/list/{eventId}")
    public Mono<EventResponse> getEventById(@PathVariable("eventId") UUID eventId) {
        return eventService.getEventById(eventId);
    }
}
