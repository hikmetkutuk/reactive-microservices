package com.microservice.reactive.attendanceservice.service;

import com.microservice.reactive.attendanceservice.dto.AttendanceRequest;
import com.microservice.reactive.attendanceservice.dto.EventResponse;
import com.microservice.reactive.attendanceservice.model.Attendance;
import com.microservice.reactive.attendanceservice.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final Logger log = LoggerFactory.getLogger(AttendanceService.class);
    private final WebClient.Builder webClientBuilder;

    public AttendanceService(AttendanceRepository attendanceRepository, WebClient.Builder webClientBuilder) {
        this.attendanceRepository = attendanceRepository;
        this.webClientBuilder = webClientBuilder;
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

    public Flux<EventResponse> getEventsByUserId(UUID userId) {
        WebClient webClient = webClientBuilder.build();

        return attendanceRepository.findByUserId(userId)
                .flatMap(attendance -> {
                    if (attendance == null) {
                        return Flux.empty();
                    } else {
                        return webClient.get()
                                .uri("http://localhost:8092/api/v1/event/list/{eventId}", attendance.getEventId())
                                .retrieve()
                                .bodyToMono(EventResponse.class)
                                .map(eventResponse -> new EventResponse(
                                        eventResponse.id(),
                                        eventResponse.eventName(),
                                        eventResponse.eventDate(),
                                        eventResponse.location(),
                                        eventResponse.description(),
                                        eventResponse.category()
                                ));
                    }
                })
                .doOnTerminate(() -> log.info("Attendances fetching process completed"))
                .onErrorResume(DataAccessException.class, e -> {
                    log.error("An error occurred while fetching attendances by user ID from the database " + e);
                    return Flux.empty();
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("An error occurred while fetching event information from the event service", e);
                    return Flux.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("An unexpected error occurred while fetching attendances by user ID", e);
                    return Flux.empty();
                });
    }
}
