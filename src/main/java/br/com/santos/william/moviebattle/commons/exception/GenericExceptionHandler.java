package br.com.santos.william.moviebattle.commons.exception;

import br.com.santos.william.moviebattle.battle.exception.BattleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Map;

@ControllerAdvice
public class GenericExceptionHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request
    ) {
        log.warn("Unhandled exception:", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is invalid");
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.warn("Unhandled exception:", ex);
        var errors = new ArrayList<Map<String, String>>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(Map.of(error.getField(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(Map.of(error.getObjectName(), error.getDefaultMessage()));
        }

        var body = Map.of(
                "code", HttpStatus.BAD_REQUEST,
                "errors", errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(value = {BattleException.class})
    protected ResponseEntity<Object> handleBattleException(
            BattleException ex,
            WebRequest request
    ) {
        log.warn("Unhandled exception:", ex);
        var body = Map.of(
                "code", HttpStatus.UNPROCESSABLE_ENTITY,
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}