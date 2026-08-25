package io.github.arubaid.user.auth.dto;

import io.github.arubaid.testsupport.validation.ValidationTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ForgotPasswordRequestTest extends ValidationTest {

    @Test
    void shouldPassValidationForValidRequest() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("john@example.com")
                .build();

        Set<ConstraintViolation<ForgotPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("")
                .build();

        Set<ConstraintViolation<ForgotPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Email is required");
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<ForgotPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Invalid email format");
    }
}