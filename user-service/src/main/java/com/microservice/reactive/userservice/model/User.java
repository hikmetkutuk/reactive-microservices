package com.microservice.reactive.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {
    @Id
    private UUID id;

    @Indexed(unique = true)
    private String email;

    private String firstname;

    private String lastname;

    @JsonIgnore
    private String password;

    @Builder.Default()
    private boolean active = true;

    @Builder.Default()
    private boolean deleted = false;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Builder.Default()
    private boolean validated = false;

    @Builder.Default()
    private List<String> roles = new ArrayList<>();
}
