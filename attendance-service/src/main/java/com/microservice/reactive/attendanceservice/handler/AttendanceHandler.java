package com.microservice.reactive.attendanceservice.handler;

import com.microservice.reactive.attendanceservice.dto.AttendanceRequest;
import com.microservice.reactive.attendanceservice.dto.EventResponse;
import com.microservice.reactive.attendanceservice.service.AttendanceService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AttendanceHandler {
    private final AttendanceService attendanceService;

    public AttendanceHandler(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public Mono<ServerResponse> handleAttendEvent(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(AttendanceRequest.class)
                .flatMap(attendanceService::attendEvent)
                .flatMap(savedAttendance -> ServerResponse.ok().bodyValue(savedAttendance))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> handleGetEventsByUserId(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("userId"));
        Flux<EventResponse> events = attendanceService.getEventsByUserId(userId);
        return ServerResponse.ok().body(events, EventResponse.class);
    }
}
