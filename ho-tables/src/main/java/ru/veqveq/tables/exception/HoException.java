package ru.veqveq.tables.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HoException extends RuntimeException{
    public HoException(String message) {
        super(message);
    }
}
