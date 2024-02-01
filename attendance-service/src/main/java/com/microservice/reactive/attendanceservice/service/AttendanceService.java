package com.microservice.reactive.attendanceservice.service;

import com.microservice.reactive.attendanceservice.dto.request.AttendanceRequest;
import com.microservice.reactive.attendanceservice.model.Attendance;
import com.microservice.reactive.attendanceservice.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Mono<ResponseEntity<?>> joinEvent(AttendanceRequest attendanceRequest) {
        Attendance newAttendance = Attendance.builder()
                .id(UUID.randomUUID())
                .eventId(attendanceRequest.eventId())
                .userId(attendanceRequest.userId())
                .attendanceDate(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .build();

        return attendanceRepository.save(newAttendance)
                .doOnSuccess(response -> log.info("Attendance created successfully with event and user: {}{}", attendanceRequest.eventId(), attendanceRequest.userId()))
                .doOnError(DataAccessException.class, e -> log.error("An error occurred while saving attendance to the database {}", e.getMessage()))
                .map(salon -> ResponseEntity.status(HttpStatus.CREATED).body(salon));
    }
}
