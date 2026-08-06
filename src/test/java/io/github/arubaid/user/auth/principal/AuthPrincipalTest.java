package io.github.arubaid.user.auth.principal;

import io.github.arubaid.user.profile.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthPrincipalTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "encodedPassword";

    // ---------- isEnabled() Tests ----------

    @ParameterizedTest
    @CsvSource(
            value = {
                    "ACTIVE, true",
                    "INACTIVE, false",
                    "SUSPENDED, false",
                    "DELETED, false",
                    "null, false"
            },
            nullValues = "null"
    )
    void shouldReturnCorrectEnabledStatus(UserStatus status, boolean expected) {
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID, EMAIL, PASSWORD, status
        );

        assertEquals(expected, principal.isEnabled());
    }

    // ---------- getAuthorities() Tests ----------

    @Test
    void shouldReturnEmptyAuthorities() {
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID, EMAIL, PASSWORD, UserStatus.ACTIVE
        );

        assertTrue(principal.getAuthorities().isEmpty());
    }

    // ---------- getUsername() Tests ----------

    @Test
    void shouldReturnEmailAsUsername() {
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID, EMAIL, PASSWORD, UserStatus.ACTIVE
        );

        assertEquals(EMAIL, principal.getUsername());
    }

    // ---------- getPassword() Tests ----------

    @Test
    void shouldReturnPassword() {
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID, EMAIL, PASSWORD, UserStatus.ACTIVE
        );

        assertEquals(PASSWORD, principal.getPassword());
    }

    // ---------- Record Accessors ----------

    @Test
    void shouldExposeRecordValues() {
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID, EMAIL, PASSWORD, UserStatus.ACTIVE
        );

        assertAll(
                () -> assertEquals(USER_ID, principal.id()),
                () -> assertEquals(EMAIL, principal.email()),
                () -> assertEquals(PASSWORD, principal.password()),
                () -> assertEquals(UserStatus.ACTIVE, principal.status())
        );
    }
}