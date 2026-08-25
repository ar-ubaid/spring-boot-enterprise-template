package io.github.arubaid.common.notification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class LoggingMailSenderTest {

    @Test
    void shouldSendWithoutThrowing() {
        MailProperties properties =
                new MailProperties();

        properties.setFrom("no-reply@example.com");

        MailSender mailSender =
                new LoggingMailSender(properties);

        assertThatCode(() ->
                mailSender.send(
                        "user@example.com",
                        "Test subject",
                        "Sensitive body"
                )
        ).doesNotThrowAnyException();
    }
}