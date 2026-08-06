package io.github.arubaid.user.profile.service;

import io.github.arubaid.user.auth.principal.AuthPrincipal;
import io.github.arubaid.user.profile.dto.UserInfo;
import io.github.arubaid.user.profile.entity.User;
import io.github.arubaid.user.profile.exception.UserNotFoundException;
import io.github.arubaid.user.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserInfo getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new AuthPrincipal(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getStatus()
                ))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + email
                        )
                );
    }
}