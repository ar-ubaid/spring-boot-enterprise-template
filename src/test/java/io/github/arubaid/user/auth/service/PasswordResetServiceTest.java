package io.github.arubaid.user.auth.service;

import io.github.arubaid.common.exception.InvalidPasswordResetTokenException;
import io.github.arubaid.user.auth.entity.PasswordResetToken;
import io.github.arubaid.user.auth.notification.PasswordResetNotifier;
import io.github.arubaid.user.auth.notification.PasswordResetProperties;
import io.github.arubaid.user.auth.repository.PasswordResetTokenRepository;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.entity.UserStatus;
import io.github.arubaid.user.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    private static final String TEST_EMAIL = "john@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded-password";

    private static final Duration TOKEN_EXPIRATION =
            Duration.ofMinutes(15);

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetNotifier passwordResetNotifier;

    @Mock
    private PasswordResetProperties passwordResetProperties;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(USER_ID)
                .email(TEST_EMAIL)
                .password(ENCODED_PASSWORD)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreatePasswordResetTokenForExistingUser() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        // When
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        // Then
        verify(passwordResetTokenRepository)
                .deleteByUserId(USER_ID);

        verify(passwordResetTokenRepository)
                .save(any(PasswordResetToken.class));

        verify(passwordResetNotifier)
                .sendPasswordResetNotification(
                        eq(testUser),
                        anyString()
                );
    }

    @Test
    void shouldSendRawTokenAndPersistOnlyItsHash() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);

        ArgumentCaptor<String> rawTokenCaptor =
                ArgumentCaptor.forClass(String.class);

        // When
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        // Then
        verify(passwordResetTokenRepository)
                .save(tokenCaptor.capture());

        verify(passwordResetNotifier)
                .sendPasswordResetNotification(
                        eq(testUser),
                        rawTokenCaptor.capture()
                );

        String rawToken = rawTokenCaptor.getValue();
        PasswordResetToken savedToken = tokenCaptor.getValue();

        assertThat(rawToken)
                .isNotBlank();

        assertThat(savedToken.getTokenHash())
                .isEqualTo(sha256(rawToken))
                .isNotEqualTo(rawToken);

        assertThat(savedToken.getTokenHash())
                .hasSize(64);
    }

    @Test
    void shouldCreateUnexpiredToken() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        when(passwordResetProperties.getExpiration())
                .thenReturn(Duration.ofMinutes(15));

        Instant before = Instant.now()
                .plus(TOKEN_EXPIRATION)
                .minusSeconds(1);

        Instant after = Instant.now()
                .plus(TOKEN_EXPIRATION)
                .plusSeconds(1);

        ArgumentCaptor<PasswordResetToken> captor =
                ArgumentCaptor.forClass(PasswordResetToken.class);

        // When
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        // Then
        verify(passwordResetTokenRepository)
                .save(captor.capture());

        PasswordResetToken savedToken = captor.getValue();

        assertThat(savedToken.getExpiresAt())
                .isAfter(before)
                .isBefore(after);

        assertThat(savedToken.getUsedAt())
                .isNull();
    }

    @Test
    void shouldDeletePreviousTokensBeforeCreatingNewToken() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        // When
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        // Then
        var inOrder = inOrder(passwordResetTokenRepository);

        inOrder.verify(passwordResetTokenRepository)
                .deleteByUserId(USER_ID);

        inOrder.verify(passwordResetTokenRepository)
                .save(any(PasswordResetToken.class));
    }

    @Test
    void shouldPropagateNotificationFailure() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        doThrow(new IllegalStateException("Notification failed"))
                .when(passwordResetNotifier)
                .sendPasswordResetNotification(
                        eq(testUser),
                        anyString()
                );

        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.requestPasswordReset(TEST_EMAIL))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Notification failed");

        verify(passwordResetTokenRepository)
                .save(any(PasswordResetToken.class));

        verify(passwordResetNotifier)
                .sendPasswordResetNotification(
                        eq(testUser),
                        anyString()
                );
    }

    @Test
    void shouldNormalizeEmailBeforeLookup() {
        // Given
        String rawEmail = "  John@Example.COM  ";

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        // When
        passwordResetService.requestPasswordReset(rawEmail);

        // Then
        verify(userRepository)
                .findByEmail(TEST_EMAIL);
    }

    @Test
    void shouldNotCreateTokenWhenUserDoesNotExist() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        // When
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        // Then
        verify(passwordResetTokenRepository, never())
                .deleteByUserId(any(UUID.class));

        verify(passwordResetTokenRepository, never())
                .save(any(PasswordResetToken.class));

        verifyNoInteractions(passwordResetNotifier);
    }

    @Test
    void shouldNotRevealWhetherEmailExistsWhenEmailIsUnknown() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        // When & Then
        passwordResetService.requestPasswordReset(TEST_EMAIL);

        verify(userRepository)
                .findByEmail(TEST_EMAIL);
    }

    @Test
    void shouldIgnoreNullEmail() {
        // When
        passwordResetService.requestPasswordReset(null);

        // Then
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordResetTokenRepository);
        verifyNoInteractions(passwordResetNotifier);
    }

    @Test
    void shouldIgnoreBlankEmail() {
        // When
        passwordResetService.requestPasswordReset("   ");

        // Then
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordResetTokenRepository);
        verifyNoInteractions(passwordResetNotifier);
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        // Given
        String rawToken = "valid-reset-token";
        String tokenHash = sha256(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(TOKEN_EXPIRATION))
                .build();

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(resetToken));

        when(passwordEncoder.encode(TEST_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        // When
        passwordResetService.resetPassword(
                rawToken,
                TEST_PASSWORD
        );

        // Then
        assertThat(testUser.getPassword())
                .isEqualTo(ENCODED_PASSWORD);

        assertThat(resetToken.getUsedAt())
                .isNotNull();

        verify(passwordEncoder)
                .encode(TEST_PASSWORD);

        verify(userRepository)
                .save(testUser);

        verify(passwordResetTokenRepository)
                .save(resetToken);
    }

    @Test
    void shouldRejectUnknownResetToken() {
        // Given
        String rawToken = "unknown-token";

        when(passwordResetTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        rawToken,
                        TEST_PASSWORD
                ))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage("Invalid or expired password reset token");

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    @Test
    void shouldRejectExpiredResetToken() {
        // Given
        String rawToken = "expired-token";

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(testUser)
                .tokenHash(sha256(rawToken))
                .expiresAt(Instant.now().minusSeconds(1))
                .build();

        when(passwordResetTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.of(resetToken));

        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        rawToken,
                        TEST_PASSWORD
                ))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage("Invalid or expired password reset token");

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    @Test
    void shouldRejectUsedResetToken() {
        // Given
        String rawToken = "used-token";

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(testUser)
                .tokenHash(sha256(rawToken))
                .expiresAt(Instant.now().plus(TOKEN_EXPIRATION))
                .usedAt(Instant.now().minusSeconds(10))
                .build();

        when(passwordResetTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.of(resetToken));

        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        rawToken,
                        TEST_PASSWORD
                ))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage("Invalid or expired password reset token");

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never())
                .save(any(PasswordResetToken.class));
    }

    @Test
    void shouldRejectBlankResetToken() {
        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        "   ",
                        TEST_PASSWORD
                ))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage("Invalid or expired password reset token");

        verifyNoInteractions(
                passwordResetTokenRepository,
                passwordEncoder,
                userRepository
        );
    }

    @Test
    void shouldRejectBlankNewPassword() {
        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        "valid-token",
                        "   "
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be blank");

        verifyNoInteractions(
                passwordResetTokenRepository,
                passwordEncoder,
                userRepository
        );
    }

    @Test
    void shouldRejectShortNewPassword() {
        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.resetPassword(
                        "valid-token",
                        "12345"
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be at least 6 characters long");

        verifyNoInteractions(
                passwordResetTokenRepository,
                passwordEncoder,
                userRepository
        );
    }

    private String sha256(String value) {
        try {
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}