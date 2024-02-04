package com.microservice.reactive.eventservice.router;

import com.microservice.reactive.eventservice.handler.EventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EventRouter {
    public static final String EVENT_ROUTE = "/api/v1/event";
    public static final String EVENT_CREATE = EVENT_ROUTE + "/create";
    public static final String EVENT_LIST = EVENT_ROUTE + "/list";
    public static final String EVENT_LIST_BY_ID = EVENT_ROUTE + "/list/{eventId}";
    private final EventHandler eventHandler;

    public EventRouter(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> eventRoutes() {
        return route()
                .POST(EVENT_CREATE, accept(APPLICATION_JSON), eventHandler::handleCreateEvent)
                .GET(EVENT_LIST, accept(APPLICATION_JSON), eventHandler::handleGetAllEvents)
                .GET(EVENT_LIST_BY_ID, accept(APPLICATION_JSON), eventHandler::handleGetEventById)
                .build();
    }
}
