package com.microservice.reactive.eventservice.repository;

import com.microservice.reactive.eventservice.model.Event;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface EventRepository extends ReactiveMongoRepository<Event, UUID> {
}
