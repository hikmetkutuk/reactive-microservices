package com.microservice.reactive.attendanceservice.router;

import com.microservice.reactive.attendanceservice.handler.AttendanceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AttendanceRouter {
    public static final String ATTENDANCE_ROUTE = "/api/v1/attendance";
    public static final String ATTENDANCE_JOIN_EVENT = ATTENDANCE_ROUTE + "/join-event";
    private final AttendanceHandler attendanceHandler;

    public AttendanceRouter(AttendanceHandler attendanceHandler) {
        this.attendanceHandler = attendanceHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> attendanceRoutes() {
        return route()
                .POST(ATTENDANCE_JOIN_EVENT, accept(APPLICATION_JSON), attendanceHandler::handleJoinEvent)
                .build();
    }
}
