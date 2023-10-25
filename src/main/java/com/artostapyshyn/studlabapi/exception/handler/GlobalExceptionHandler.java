package com.artostapyshyn.studlabapi.exception.handler;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.SignatureException;

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

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Object> handleMalformedJwtException(MalformedJwtException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("io.jsonwebtoken.MalformedJwtException: JWT strings must contain exactly 2 period characters. Found: 0")) {
            return ResponseEntity.internalServerError().body("JWT error");
        }
        throw ex;
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Object> handleSignatureException(SignatureException ex) throws SignatureException {
        if (ex.getMessage() != null && ex.getMessage().contains("io.jsonwebtoken.security.SignatureException: " +
                "JWT signature does not match locally computed signature. " +
                "JWT validity cannot be asserted and should not be trusted.")) {
            return ResponseEntity.internalServerError().body("JWT expired and used again");
        }
        throw ex;
    }
}
