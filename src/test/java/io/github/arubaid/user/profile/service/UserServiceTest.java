package io.github.arubaid.user.profile.service;

import io.github.arubaid.user.auth.principal.AuthPrincipal;
import io.github.arubaid.user.profile.dto.UserInfo;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.entity.UserStatus;
import io.github.arubaid.user.profile.exception.UserNotFoundException;
import io.github.arubaid.user.profile.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ---------- getCurrentUser() Tests ----------

    @Test
    void shouldReturnCurrentUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserInfo result = userService.getCurrentUser(USER_ID);

        assertAll(
                () -> assertEquals(USER_ID, result.getId()),
                () -> assertEquals(EMAIL, result.getEmail()),
                () -> assertEquals(UserStatus.ACTIVE, result.getStatus())
        );

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenCurrentUserDoesNotExist() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getCurrentUser(USER_ID)
        );

        assertEquals(
                "User not found with id: " + USER_ID,
                exception.getMessage()
        );

        verify(userRepository).findById(USER_ID);
    }

    // ---------- loadUserByUsername() Tests ----------

    @Test
    void shouldLoadUserByEmail() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(EMAIL);

        assertInstanceOf(AuthPrincipal.class, result);

        AuthPrincipal principal = (AuthPrincipal) result;

        assertAll(
                () -> assertEquals(USER_ID, principal.id()),
                () -> assertEquals(EMAIL, principal.email()),
                () -> assertEquals(PASSWORD, principal.password()),
                () -> assertEquals(UserStatus.ACTIVE, principal.status())
        );

        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(EMAIL)
        );

        assertEquals(
                "User not found: " + EMAIL,
                exception.getMessage()
        );

        verify(userRepository).findByEmail(EMAIL);
    }
}