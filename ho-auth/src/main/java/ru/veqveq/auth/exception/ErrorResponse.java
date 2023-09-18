package ru.veqveq.auth.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;
    private Instant timestamp = Instant.now();
}
