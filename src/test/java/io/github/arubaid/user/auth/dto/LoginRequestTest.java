package io.github.arubaid.user.auth.dto;

import io.github.arubaid.testsupport.validation.ValidationTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest extends ValidationTest {

    @Test
    void shouldPassValidationForValidRequest() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Email is required");
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        LoginRequest request = LoginRequest.builder()
                .email("invalid-email")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Invalid email format");
    }

    @Test
    void shouldFailWhenPasswordIsBlank() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password is required");
    }
}