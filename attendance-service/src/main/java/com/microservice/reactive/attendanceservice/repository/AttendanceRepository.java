package com.microservice.reactive.attendanceservice.repository;

import com.microservice.reactive.attendanceservice.model.Attendance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AttendanceRepository extends ReactiveCrudRepository<Attendance, UUID> {
}
