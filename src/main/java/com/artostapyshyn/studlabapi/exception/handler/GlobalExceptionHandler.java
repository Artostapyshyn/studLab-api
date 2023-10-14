package com.artostapyshyn.studlabapi.exception.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("org.springframework.security.core.Authentication.getName()")) {
            return ResponseEntity.internalServerError().body("Authentication error");
        }
        throw ex;
    }
}
