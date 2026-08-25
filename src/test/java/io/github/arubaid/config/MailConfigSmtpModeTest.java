package io.github.arubaid.config;

import io.github.arubaid.common.notification.MailSender;
import io.github.arubaid.common.notification.SmtpMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = {
                "app.mail.mode=smtp",
                "app.mail.from=no-reply@example.com"
        }
)
class MailConfigSmtpModeTest {

    @Autowired
    private MailSender mailSender;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Test
    void shouldUseSmtpMailSenderWhenConfigured() {
        assertThat(mailSender)
                .isInstanceOf(SmtpMailSender.class);
    }
}