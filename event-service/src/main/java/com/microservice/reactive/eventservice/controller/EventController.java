package com.microservice.reactive.eventservice.controller;

import com.microservice.reactive.eventservice.dto.request.EventRequest;
import com.microservice.reactive.eventservice.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
