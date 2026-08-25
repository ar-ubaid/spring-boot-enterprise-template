package io.github.arubaid.user.auth.notification;

import io.github.arubaid.common.notification.MailSender;
import io.github.arubaid.user.profile.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailPasswordResetNotifierTest {

    private static final String TEST_EMAIL =
            "john@example.com";

    private static final String RESET_URL =
            "https://localhost:8080/reset-password";

    private static final String RAW_TOKEN =
            "secure-test-token";

    @Mock
    private MailSender mailSender;

    private PasswordResetNotifier notificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        PasswordResetProperties properties =
                new PasswordResetProperties();

        properties.setUrl(RESET_URL);

        notificationService =
                new EmailPasswordResetNotifier(
                        mailSender,
                        properties
                );

        testUser = User.builder()
                .email(TEST_EMAIL)
                .build();
    }

    @Test
    void shouldSendPasswordResetEmail() {
        // When
        notificationService.sendPasswordResetNotification(
                testUser,
                RAW_TOKEN
        );

        // Then
        verify(mailSender).send(
                eq(TEST_EMAIL),
                eq("Reset your Template password"),
                anyString()
        );
    }

    @Test
    void shouldIncludeResetTokenInResetUrl() {
        // Given
        ArgumentCaptor<String> bodyCaptor =
                ArgumentCaptor.forClass(String.class);

        // When
        notificationService.sendPasswordResetNotification(
                testUser,
                RAW_TOKEN
        );

        // Then
        verify(mailSender).send(
                eq(TEST_EMAIL),
                anyString(),
                bodyCaptor.capture()
        );

        assertThat(bodyCaptor.getValue())
                .contains(
                        RESET_URL + "?token=" + RAW_TOKEN
                );
    }

    @Test
    void shouldIncludeExpirationInformation() {
        // Given
        ArgumentCaptor<String> bodyCaptor =
                ArgumentCaptor.forClass(String.class);

        // When
        notificationService.sendPasswordResetNotification(
                testUser,
                RAW_TOKEN
        );

        // Then
        verify(mailSender).send(
                anyString(),
                anyString(),
                bodyCaptor.capture()
        );

        assertThat(bodyCaptor.getValue())
                .contains("15 minutes");
    }

    @Test
    void shouldSendEmailToUsersEmailAddress() {
        // When
        notificationService.sendPasswordResetNotification(
                testUser,
                RAW_TOKEN
        );

        // Then
        verify(mailSender).send(
                eq(TEST_EMAIL),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldNotSendMoreThanOneEmail() {
        // When
        notificationService.sendPasswordResetNotification(
                testUser,
                RAW_TOKEN
        );

        // Then
        verify(mailSender, times(1))
                .send(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }
}