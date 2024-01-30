package com.microservice.reactive.userservice.repository;

import com.microservice.reactive.userservice.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
    Mono<User> findByEmail(@Param("email") String email);

    @Query("{'id': ?0, 'deleted': false}")
    Mono<User> findByIdWithLimit(UUID id);

    Flux<User> findAllByDeletedFalse();
}
