package io.github.arubaid.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(FeatureNotImplementedException.class)
    public ResponseEntity<String> handleFeatureNotImplemented(
            FeatureNotImplementedException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(exception.getMessage());
    }
}
