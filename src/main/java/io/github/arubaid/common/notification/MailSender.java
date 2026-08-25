package io.github.arubaid.common.notification;

public interface MailSender {

    void send(
            String to,
            String subject,
            String body
    );
}