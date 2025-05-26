package pl.edu.pwr.commandservice.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> illegalArgumentException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("Illegal ID provided in request", HttpStatus.BAD_REQUEST);
    }
}
