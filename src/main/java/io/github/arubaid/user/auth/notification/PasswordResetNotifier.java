package io.github.arubaid.user.auth.notification;

import io.github.arubaid.user.profile.entity.User;

public interface PasswordResetNotifier {

    void sendPasswordResetNotification(
            User user,
            String rawToken
    );
}