package io.github.arubaid.user.auth.notification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.password-reset")
public class PasswordResetProperties {

    private String url;
    private Duration expiration = Duration.ofMinutes(15);
}