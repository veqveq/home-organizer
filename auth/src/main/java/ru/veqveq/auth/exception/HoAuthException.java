package ru.veqveq.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HoAuthException extends RuntimeException{
    public HoAuthException(String message) {
        super(message);
    }
}
