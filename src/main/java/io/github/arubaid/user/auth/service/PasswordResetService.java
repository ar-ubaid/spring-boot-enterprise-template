package io.github.arubaid.user.auth.service;

import io.github.arubaid.common.exception.InvalidPasswordResetTokenException;
import io.github.arubaid.user.auth.entity.PasswordResetToken;
import io.github.arubaid.user.auth.notification.PasswordResetNotifier;
import io.github.arubaid.user.auth.notification.PasswordResetProperties;
import io.github.arubaid.user.auth.repository.PasswordResetTokenRepository;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final PasswordResetProperties passwordResetProperties;

    private final PasswordResetNotifier passwordResetNotifier;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Initiates a password reset request.
     * The method deliberately does not reveal whether the supplied
     * email belongs to an existing user.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);

        userRepository.findByEmail(normalizedEmail)
                .ifPresent(this::createPasswordResetToken);
    }

    /**
     * Resets a user's password using a valid password reset token.
     * The raw token is never persisted. Only its SHA-256 hash is stored.
     */
    @Transactional
    public void resetPassword(
            String rawToken,
            String newPassword
    ) {
        validateResetPasswordInput(rawToken, newPassword);

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(this::invalidResetTokenException);

        if (!resetToken.isValid()) {
            throw invalidResetTokenException();
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        resetToken.setUsedAt(Instant.now());

        userRepository.save(user);
        passwordResetTokenRepository.save(resetToken);
    }

    private void createPasswordResetToken(User user) {
        /*
         * Only one reset token should be active for a user.
         */
        passwordResetTokenRepository.deleteByUserId(user.getId());

        /*
         * Generate a cryptographically secure token.
         *
         * This raw value exists only in memory.
         */
        String rawToken = generateRawToken();

        /*
         * Persist only the SHA-256 hash.
         */
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plus(passwordResetProperties.getExpiration()))
                .build();

        passwordResetTokenRepository.save(resetToken);

        /*
         * Pass the raw token only to the notification boundary.
         *
         * It must never be logged or persisted.
         */
        passwordResetNotifier.sendPasswordResetNotification(user, rawToken);
    }

    private String generateRawToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];

        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {
        try {
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private void validateResetPasswordInput(
            String rawToken,
            String newPassword
    ) {
        if (rawToken == null || rawToken.isBlank()) {
            throw invalidResetTokenException();
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be blank"
            );
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters long"
            );
        }
    }

    private InvalidPasswordResetTokenException invalidResetTokenException() {
        return new InvalidPasswordResetTokenException();
    }
}