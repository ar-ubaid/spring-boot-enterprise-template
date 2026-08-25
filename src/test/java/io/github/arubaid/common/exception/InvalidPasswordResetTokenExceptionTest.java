package io.github.arubaid.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPasswordResetTokenExceptionTest {

    @Test
    void shouldContainExpectedMessage() {
        InvalidPasswordResetTokenException exception =
                new InvalidPasswordResetTokenException();

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Invalid or expired password reset token"
                );
    }
}