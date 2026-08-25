package io.github.arubaid.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CommonExceptionHandlerTest {

    private final CommonExceptionHandler handler =
            new CommonExceptionHandler();

    @Test
    void shouldHandleFeatureNotImplementedException() {
        // Given
        FeatureNotImplementedException exception =
                new FeatureNotImplementedException("Get all users");

        // When
        ResponseEntity<String> response =
                handler.handleFeatureNotImplemented(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_IMPLEMENTED);

        assertThat(response.getBody())
                .isEqualTo("Get all users is coming soon.");
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid business profile");

        // When
        ResponseEntity<String> response =
                handler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isEqualTo("Invalid business profile");
    }

    @Test
    void shouldHandleInvalidPasswordResetTokenException() {
        // Given
        InvalidPasswordResetTokenException exception =
                new InvalidPasswordResetTokenException();

        // When
        ResponseEntity<String> response =
                handler.handleInvalidPasswordResetToken(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isEqualTo(
                        "INVALID_PASSWORD_RESET_TOKEN: "
                                + "Invalid or expired password reset token"
                );
    }

    @Test
    void shouldHandleUnexpectedException() {
        // Given
        Exception exception =
                new Exception("Something unexpected happened");

        // When
        ResponseEntity<String> response =
                handler.handleException(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody())
                .isEqualTo("An unexpected error occurred.");
    }
}