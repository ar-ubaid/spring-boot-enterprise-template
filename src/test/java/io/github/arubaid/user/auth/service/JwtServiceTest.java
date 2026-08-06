package io.github.arubaid.user.auth.service;

import io.github.arubaid.user.profile.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            Base64.getEncoder().encodeToString(
                    "01234567890123456789012345678901".getBytes()
            );

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
    }

    @Test
    void generateTokenShouldGenerateValidToken() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";

        String token = jwtService.generateToken(userId, email, UserStatus.ACTIVE);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsernameShouldReturnEmail() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";

        String token = jwtService.generateToken(userId, email, UserStatus.ACTIVE);

        assertEquals(email, jwtService.extractUsername(token));
    }

    @Test
    void extractUserIdShouldReturnUserId() {
        UUID userId = UUID.randomUUID();

        String token = jwtService.generateToken(userId, "user@example.com", UserStatus.ACTIVE);

        assertEquals(userId, jwtService.extractUserId(token));
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(
                UUID.randomUUID(),
                "user@example.com",
                UserStatus.ACTIVE
        );

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValidShouldReturnFalseForMalformedToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.value"));
    }

    @Test
    void isTokenValidShouldReturnFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);

        String token = jwtService.generateToken(
                UUID.randomUUID(),
                "user@example.com",
                UserStatus.ACTIVE
        );

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void extractUsernameShouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class,
                () -> jwtService.extractUsername("invalid.token"));
    }

    @Test
    void extractUserIdShouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class,
                () -> jwtService.extractUserId("invalid.token"));
    }
}