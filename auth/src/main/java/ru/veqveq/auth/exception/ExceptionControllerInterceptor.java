package ru.veqveq.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionControllerInterceptor {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException exception) {
        String message = StringUtils.join(exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.toSet()), "; ");
        log.error(message);
        ErrorResponse response = new ErrorResponse(HttpStatus.PROCESSING, message);
        return new ResponseEntity<>(response, HttpStatus.PROCESSING);
    }

    @ExceptionHandler(HoAuthException.class)
    public ResponseEntity<ErrorResponse> handleHoException(HoAuthException exception) {
        log.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHoNotFoundException(HoNotFoundException exception) {
        log.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
