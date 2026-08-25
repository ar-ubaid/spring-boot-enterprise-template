package io.github.arubaid.user.auth.dto;

import io.github.arubaid.testsupport.validation.ValidationTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ResetPasswordRequestTest extends ValidationTest {

    @Test
    void shouldPassValidationForValidRequest() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-reset-token")
                .newPassword("password123")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenTokenIsBlank() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("")
                .newPassword("password123")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Token is required");
    }

    @Test
    void shouldFailWhenPasswordIsBlank() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-reset-token")
                .newPassword("")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password is required");
    }

    @Test
    void shouldFailWhenPasswordIsTooShort() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-reset-token")
                .newPassword("12345")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must be at least 6 characters");
    }
}