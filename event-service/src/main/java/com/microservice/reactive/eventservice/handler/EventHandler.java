package com.microservice.reactive.eventservice.handler;

import com.microservice.reactive.eventservice.dto.request.EventRequest;
import com.microservice.reactive.eventservice.dto.response.EventResponse;
import com.microservice.reactive.eventservice.service.EventService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EventHandler {
    private final EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }

    public Mono<ServerResponse> handleCreateEvent(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(EventRequest.class)
                .flatMap(eventService::createEvent)
                .flatMap(createdEvent -> ServerResponse.ok().bodyValue(createdEvent))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> handleGetAllEvents(ServerRequest request) {
        Flux<EventResponse> events = eventService.getAllEvents();
        return ServerResponse.ok().body(events, EventResponse.class);
    }
}
