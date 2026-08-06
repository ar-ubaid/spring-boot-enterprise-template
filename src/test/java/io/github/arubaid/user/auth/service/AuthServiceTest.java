package io.github.arubaid.user.auth.service;

import io.github.arubaid.user.auth.dto.AuthResponse;
import io.github.arubaid.user.auth.dto.LoginRequest;
import io.github.arubaid.user.auth.dto.RegisterRequest;
import io.github.arubaid.user.auth.principal.AuthPrincipal;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.entity.UserStatus;
import io.github.arubaid.user.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String TEST_EMAIL = "john@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final UserStatus TEST_STATUS = UserStatus.ACTIVE;
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String JWT_TOKEN = "jwt-token";

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        loginRequest = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        testUser = User.builder()
                .id(USER_ID)
                .email(TEST_EMAIL)
                .password(ENCODED_PASSWORD)
                .status(UserStatus.ACTIVE)
                .build();
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(USER_ID, TEST_EMAIL, TEST_STATUS)).thenReturn(JWT_TOKEN);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getUser().getId()).isEqualTo(USER_ID);
        assertThat(response.getUser().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.getUser().getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(USER_ID, TEST_EMAIL, TEST_STATUS);
    }

    @Test
    void shouldCreateUserWithCorrectAttributes() {
        // Given
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(UUID.class), anyString(), any(UserStatus.class))).thenReturn(JWT_TOKEN);

        // When
        authService.register(registerRequest);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldNormalizeEmailBeforeSaving() {
        // Given
        String rawEmail = "  John@Example.COM  ";
        String normalizedEmail = "john@example.com";

        RegisterRequest request = RegisterRequest.builder()
                .email(rawEmail)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        User userWithNormalizedEmail = User.builder()
                .id(USER_ID)
                .email(normalizedEmail)
                .password(ENCODED_PASSWORD)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithNormalizedEmail);
        when(jwtService.generateToken(any(UUID.class), anyString(), any(UserStatus.class))).thenReturn(JWT_TOKEN);

        // When
        authService.register(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(normalizedEmail);
        verify(userRepository).existsByEmail(normalizedEmail);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateToken(any(UUID.class), anyString(), any(UserStatus.class));
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void shouldLoginSuccessfully() {

        AuthPrincipal principal = new AuthPrincipal(
                USER_ID,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_STATUS
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(USER_ID, TEST_EMAIL, TEST_STATUS)).thenReturn(JWT_TOKEN);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getUser().getId()).isEqualTo(USER_ID);
        assertThat(response.getUser().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.getUser().getStatus()).isEqualTo(TEST_STATUS);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(USER_ID, TEST_EMAIL, TEST_STATUS);
    }

    @Test
    void shouldNormalizeEmailBeforeLookup() {
        // Given
        String rawEmail = "  John@Example.COM  ";
        String normalizedEmail = "john@example.com";

        LoginRequest request = LoginRequest.builder()
                .email(rawEmail)
                .password(TEST_PASSWORD)
                .build();

        // Create AuthPrincipal with normalized email
        AuthPrincipal principal = new AuthPrincipal(
                USER_ID,
                normalizedEmail,
                ENCODED_PASSWORD,
                UserStatus.ACTIVE
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(any(UUID.class), anyString(), any(UserStatus.class))).thenReturn(JWT_TOKEN);

        // When
        authService.login(request);

        // Then
        // Verify the authentication was called with normalized email
        verify(authenticationManager).authenticate(
                argThat(token -> {
                    UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) token;

                    // The principal passed to authenticate() is the email String
                    // (not yet converted to AuthPrincipal)
                    String principalString = (String) authToken.getPrincipal();

                    return principalString != null && principalString.equals(normalizedEmail);
                })
        );

        // Verify JWT was generated with normalized email
        verify(jwtService).generateToken(USER_ID, normalizedEmail, UserStatus.ACTIVE);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        // Mock the authentication manager to throw BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");

        // Verify that authentication manager was called
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        // Verify that JWT service was NOT called
        verify(jwtService, never()).generateToken(any(UUID.class), anyString(), any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UUID.class), anyString(), any(UserStatus.class));
    }
}