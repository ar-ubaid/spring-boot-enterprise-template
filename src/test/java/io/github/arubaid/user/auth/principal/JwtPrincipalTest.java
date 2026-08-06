package io.github.arubaid.user.auth.principal;

import io.github.arubaid.user.profile.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtPrincipalTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = null;

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
        JwtPrincipal principal = new JwtPrincipal(
                USER_ID, EMAIL, status
        );

        assertEquals(expected, principal.isEnabled());
    }

    // ---------- getAuthorities() Tests ----------

    @Test
    void shouldReturnEmptyAuthorities() {
        JwtPrincipal principal = new JwtPrincipal(
                USER_ID, EMAIL, UserStatus.ACTIVE
        );

        assertTrue(principal.getAuthorities().isEmpty());
    }

    // ---------- getUsername() Tests ----------

    @Test
    void shouldReturnEmailAsUsername() {
        JwtPrincipal principal = new JwtPrincipal(
                USER_ID, EMAIL, UserStatus.ACTIVE
        );

        assertEquals(EMAIL, principal.getUsername());
    }

    // ---------- getPassword() Tests ----------

    @Test
    void shouldReturnPassword() {
        JwtPrincipal principal = new JwtPrincipal(
                USER_ID, EMAIL, UserStatus.ACTIVE
        );

        assertEquals(PASSWORD, principal.getPassword());
    }

    // ---------- Record Accessors ----------

    @Test
    void shouldExposeRecordValues() {
        JwtPrincipal principal = new JwtPrincipal(
                USER_ID, EMAIL, UserStatus.ACTIVE
        );

        assertAll(
                () -> assertEquals(USER_ID, principal.id()),
                () -> assertEquals(EMAIL, principal.email()),
                () -> assertEquals(PASSWORD, principal.getPassword()),
                () -> assertEquals(UserStatus.ACTIVE, principal.status())
        );
    }
}
