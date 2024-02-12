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
    public static final String ATTEND_EVENT = ATTENDANCE_ROUTE + "/event";
    public static final String ATTENDANCE_EVENT_LIST = ATTENDANCE_ROUTE + "/event/list/{userId}";
    public static final String ATTENDANCE_USER_LIST = ATTENDANCE_ROUTE + "/user/list/{eventId}";
    private final AttendanceHandler attendanceHandler;

    public AttendanceRouter(AttendanceHandler attendanceHandler) {
        this.attendanceHandler = attendanceHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> attendanceRoutes() {
        return route()
                .POST(ATTEND_EVENT, accept(APPLICATION_JSON), attendanceHandler::handleAttendEvent)
                .GET(ATTENDANCE_EVENT_LIST, accept(APPLICATION_JSON), attendanceHandler::handleGetEventsByUserId)
                .GET(ATTENDANCE_USER_LIST, accept(APPLICATION_JSON), attendanceHandler::handleGetUsersByEventId)
                .build();
    }
}
