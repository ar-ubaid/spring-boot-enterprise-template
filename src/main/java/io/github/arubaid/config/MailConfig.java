package io.github.arubaid.config;

import io.github.arubaid.common.notification.LoggingMailSender;
import io.github.arubaid.common.notification.MailProperties;
import io.github.arubaid.common.notification.MailSender;
import io.github.arubaid.common.notification.SmtpMailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "app.mail",
            name = "mode",
            havingValue = "smtp"
    )
    public MailSender smtpMailSender(
            JavaMailSender javaMailSender,
            MailProperties mailProperties
    ) {
        return new SmtpMailSender(
                javaMailSender,
                mailProperties
        );
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "app.mail",
            name = "mode",
            havingValue = "log",
            matchIfMissing = true
    )
    public MailSender loggingMailSender(
            MailProperties mailProperties
    ) {
        return new LoggingMailSender(
                mailProperties
        );
    }
}