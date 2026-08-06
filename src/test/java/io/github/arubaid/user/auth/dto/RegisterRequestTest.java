package io.github.arubaid.user.auth.dto;

import io.github.arubaid.testsupport.validation.ValidationTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class RegisterRequestTest extends ValidationTest {

    @Test
    void shouldPassValidationForValidRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<RegisterRequest>>
                violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenPasswordTooShort() {
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password("12345")
                .build();

        Set<ConstraintViolation<RegisterRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must be at least 6 characters long");
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .email("abc")
                .password("password123")
                .build();

        Set<ConstraintViolation<RegisterRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Invalid email format");
    }
}