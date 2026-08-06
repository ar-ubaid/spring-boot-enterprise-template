package io.github.arubaid.user.auth.principal;

import io.github.arubaid.user.profile.entity.UserStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Represents the authenticated user stored in the Spring Security context.
 *
 * <p>Instances are reconstructed from validated JWT claims and therefore do
 * not contain the user's password.</p>
 *
 * @param id immutable identifier of the authenticated user
 * @param email login identifier (username) of the authenticated user
 */
public record JwtPrincipal(
        UUID id,
        String email,
        UserStatus status
) implements UserDetails {

    /**
     * Returns the authorities granted to the authenticated user.
     *
     * <p>No authorities are assigned in the current implementation. This can be
     * extended to return roles or permissions when authorization is introduced.</p>
     *
     * @return an immutable collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * <p>This principal is reconstructed from validated JWT claims after
     * authentication has already completed, so no password is available or
     * required.</p>
     *
     * @return {@code null}, as JWT-authenticated principals do not carry passwords
     */
    @Override
    @Nullable
    public String getPassword() {
        return null;
    }

    /**
     * Returns the login identifier of the authenticated user.
     *
     * <p>Although named {@code username} by Spring Security, the application's
     * login identifier is the user's email address.</p>
     *
     * @return the authenticated user's email address
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() { return status == UserStatus.ACTIVE; }
}