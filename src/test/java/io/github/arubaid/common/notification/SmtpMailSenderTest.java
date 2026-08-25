package io.github.arubaid.common.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmtpMailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    private SmtpMailSender smtpMailSender;

    @BeforeEach
    void setUp() {
        MailProperties properties =
                new MailProperties();

        properties.setFrom(
                "no-reply@example.com"
        );

        smtpMailSender =
                new SmtpMailSender(
                        javaMailSender,
                        properties
                );
    }

    @Test
    void shouldSendEmailThroughJavaMailSender() {
        // When
        smtpMailSender.send(
                "user@example.com",
                "Test subject",
                "Test body"
        );

        // Then
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class
                );

        verify(javaMailSender)
                .send(captor.capture());

        SimpleMailMessage message =
                captor.getValue();

        assertThat(message.getFrom())
                .isEqualTo("no-reply@example.com");

        assertThat(message.getTo())
                .containsExactly("user@example.com");

        assertThat(message.getSubject())
                .isEqualTo("Test subject");

        assertThat(message.getText())
                .isEqualTo("Test body");
    }
}