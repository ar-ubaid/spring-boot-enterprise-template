package io.github.arubaid.user.profile.controller;

import io.github.arubaid.security.SecurityConfig;
import io.github.arubaid.user.auth.principal.JwtPrincipal;
import io.github.arubaid.user.auth.service.JwtService;
import io.github.arubaid.user.profile.dto.UserInfo;
import io.github.arubaid.user.profile.entity.UserStatus;
import io.github.arubaid.user.profile.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtService jwtService;

    @Test
    void shouldReturnNotImplementedForTestEndpoint() throws Exception {
        UUID userId = UUID.randomUUID();

        JwtPrincipal principal = new JwtPrincipal(
                userId,
                "test@example.com",
                UserStatus.ACTIVE
        );

        mockMvc.perform(get("/api/v1/users/all")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        principal,
                                        null,
                                        principal.getAuthorities()
                                )
                        ))
                )
                .andExpect(status().isNotImplemented())
                .andExpect(content().string("all is coming soon."));
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        UUID userId = UUID.randomUUID();

        JwtPrincipal principal = new JwtPrincipal(
                userId,
                "test@example.com",
                UserStatus.ACTIVE
        );

        UserInfo userInfo = new UserInfo(
                userId,
                "test@example.com",
                UserStatus.ACTIVE
        );

        when(userService.getCurrentUser(userId))
                .thenReturn(userInfo);

        mockMvc.perform(
                        get("/api/v1/users/me")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                principal,
                                                null,
                                                principal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).getCurrentUser(userId);
    }
}