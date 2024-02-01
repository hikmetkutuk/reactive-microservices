package com.microservice.reactive.attendanceservice.repository;

import com.microservice.reactive.attendanceservice.model.Attendance;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface AttendanceRepository extends ReactiveMongoRepository<Attendance, UUID> {
}
