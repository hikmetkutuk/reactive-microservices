package com.microservice.reactive.attendanceservice.service;

import com.microservice.reactive.attendanceservice.dto.AttendanceRequest;
import com.microservice.reactive.attendanceservice.model.Attendance;
import com.microservice.reactive.attendanceservice.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Mono<String> attendEvent(AttendanceRequest attendanceRequest) {
        Attendance newAttendance = Attendance.builder()
                .id(UUID.randomUUID())
                .userId(attendanceRequest.userId())
                .eventId(attendanceRequest.eventId())
                .createdDate(LocalDateTime.now())
                .build();

        return attendanceRepository.save(newAttendance)
                .map(savedAttendance -> {
                    log.info("Attendance recorded for user {} in event {}", attendanceRequest.userId(), attendanceRequest.eventId());
                    return "Attendance recorded successfully";
                })
                .doOnError(error -> log.error("Error while recording attendance for user {} in event {}: {}", attendanceRequest.userId(), attendanceRequest.eventId(), error.getMessage()));
    }
}
