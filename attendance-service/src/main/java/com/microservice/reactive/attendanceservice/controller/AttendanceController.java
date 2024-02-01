package com.microservice.reactive.attendanceservice.controller;

import com.microservice.reactive.attendanceservice.dto.request.AttendanceRequest;
import com.microservice.reactive.attendanceservice.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/join-event")
    public Mono<ResponseEntity<?>> joinEvent(@RequestBody AttendanceRequest attendanceRequest) {
        return attendanceService.joinEvent(attendanceRequest);
    }
}
