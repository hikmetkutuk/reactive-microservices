package com.microservice.reactive.attendanceservice.handler;

import com.microservice.reactive.attendanceservice.dto.AttendanceRequest;
import com.microservice.reactive.attendanceservice.service.AttendanceService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
}
