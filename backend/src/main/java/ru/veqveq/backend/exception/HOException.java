package ru.veqveq.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HOException extends RuntimeException{
    public HOException(String message) {
        super(message);
    }
}
