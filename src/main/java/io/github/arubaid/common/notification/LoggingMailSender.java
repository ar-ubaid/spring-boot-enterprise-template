package io.github.arubaid.common.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingMailSender implements MailSender {

    private final MailProperties mailProperties;

    @Override
    public void send(
            String to,
            String subject,
            String body
    ) {
        log.info(
                "Email prepared: to={}, from={}, subject={}",
                to,
                mailProperties.getFrom(),
                subject
        );

        /*
         * Never log the body.
         *
         * Email bodies may contain:
         * - password reset tokens
         * - verification tokens
         * - invitation links
         * - other security-sensitive data
         */
    }
}