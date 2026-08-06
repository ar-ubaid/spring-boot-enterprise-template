package io.github.arubaid.user.auth.service;

import io.github.arubaid.user.auth.dto.AuthResponse;
import io.github.arubaid.user.auth.dto.LoginRequest;
import io.github.arubaid.user.auth.dto.RegisterRequest;
import io.github.arubaid.user.auth.principal.AuthPrincipal;
import io.github.arubaid.user.profile.dto.UserInfo;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.entity.UserStatus;
import io.github.arubaid.user.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getStatus());

        return AuthResponse.builder()
                .token(token)
                .user(UserInfo.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .status(savedUser.getStatus())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                normalizedEmail,
                                request.getPassword()
                        )
                );

        AuthPrincipal principal =
                (AuthPrincipal) authentication.getPrincipal();

        Optional.ofNullable(principal).orElseThrow(() -> new IllegalStateException("Authenticated principal is missing"));

        String token = jwtService.generateToken(principal.id(), principal.email(), principal.status());

        return AuthResponse.builder()
                .token(token)
                .user(UserInfo.builder()
                        .id(principal.id())
                        .email(principal.email())
                        .status(principal.status())
                        .build())
                .build();
    }
}
