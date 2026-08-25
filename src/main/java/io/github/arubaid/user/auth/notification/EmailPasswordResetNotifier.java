package io.github.arubaid.user.auth.notification;

import io.github.arubaid.common.notification.MailSender;
import io.github.arubaid.user.profile.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailPasswordResetNotifier implements PasswordResetNotifier {

    private final MailSender mailSender;
    private final PasswordResetProperties passwordResetProperties;

    @Override
    public void sendPasswordResetNotification(
            User user,
            String rawToken
    ) {
        String resetUrl = buildResetUrl(rawToken);

        String subject = "Reset your Template password";

        String body = buildEmailBody(resetUrl);

        mailSender.send(
                user.getEmail(),
                subject,
                body
        );
    }

    private String buildResetUrl(String rawToken) {
        String encodedToken = URLEncoder.encode(
                rawToken,
                StandardCharsets.UTF_8
        );

        return passwordResetProperties.getUrl()
                + "?token="
                + encodedToken;
    }

    private String buildEmailBody(String resetUrl) {
        return """
                Hello,

                We received a request to reset your Template password.

                Use the following link to reset your password:

                %s

                This link will expire after %d minutes.

                If you did not request a password reset, you can safely ignore this email.

                Regards,
                Spring Boot Template Team
                """.formatted(
                        resetUrl,
                        passwordResetProperties
                                .getExpiration()
                                .toMinutes()
                );
    }
}