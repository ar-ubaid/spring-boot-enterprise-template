package io.github.arubaid.config;

import io.github.arubaid.Application;
import io.github.arubaid.common.notification.MailSender;
import io.github.arubaid.common.notification.SmtpMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = Application.class,
        properties = {
                "app.mail.mode=smtp",
                "app.mail.from=no-reply@example.com"
        }
)
@Import(MailConfigSmtpModeTest.TestMailConfig.class)
class MailConfigSmtpModeTest {

    @Autowired
    private MailSender mailSender;

    @Test
    void shouldUseSmtpMailSenderWhenConfigured() {
        assertThat(mailSender)
                .isInstanceOf(SmtpMailSender.class);
    }

    @TestConfiguration
    static class TestMailConfig {

        @Bean
        JavaMailSender javaMailSender() {
            return org.mockito.Mockito.mock(
                    JavaMailSender.class
            );
        }
    }
}