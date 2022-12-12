package ru.veqveq.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HoNotFoundException extends RuntimeException {
    public HoNotFoundException(String message) {
        super(message);
    }
}
