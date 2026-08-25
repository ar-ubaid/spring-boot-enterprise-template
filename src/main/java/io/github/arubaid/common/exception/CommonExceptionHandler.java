package io.github.arubaid.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FeatureNotImplementedException.class)
    public ResponseEntity<String> handleFeatureNotImplemented(
            FeatureNotImplementedException exception) {

        log.debug("Feature not implemented: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        log.debug("Invalid request: {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<String> handleInvalidPasswordResetToken(
            InvalidPasswordResetTokenException exception) {

        return ResponseEntity
                .badRequest()
                .body("INVALID_PASSWORD_RESET_TOKEN: " + exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {

        log.error("Unexpected exception occurred", exception);

        return ResponseEntity
                .internalServerError()
                .body("An unexpected error occurred.");
    }
}