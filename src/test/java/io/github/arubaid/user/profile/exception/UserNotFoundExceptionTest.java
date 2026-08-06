package io.github.arubaid.user.profile.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotFoundExceptionTest {

    @Test
    void shouldCreateMessageWithUserId() {
        UUID userId = UUID.randomUUID();

        UserNotFoundException exception =
                new UserNotFoundException(userId);

        assertEquals(
                "User not found with id: " + userId,
                exception.getMessage()
        );
    }

    @Test
    void shouldCreateMessageWithEmail() {
        String email = "test@example.com";

        UserNotFoundException exception =
                new UserNotFoundException(email);

        assertEquals(
                "User not found with email: " + email,
                exception.getMessage()
        );
    }
}