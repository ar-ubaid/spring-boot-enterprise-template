package io.github.arubaid.user.auth.config;

import io.github.arubaid.user.auth.principal.JwtPrincipal;
import io.github.arubaid.user.auth.service.JwtService;
import io.github.arubaid.user.profile.entity.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);

        SecurityContextHolder.clearContext();

        // Ensure OncePerRequestFilter.shouldNotFilter() allows
        // the request to reach doFilterInternal().
        when(request.getServletPath())
                .thenReturn("/api/v1/users/me");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateActiveUser()
            throws ServletException, IOException {

        UUID userId = UUID.randomUUID();
        String email = "john@example.com";
        String jwt = "valid-jwt";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.isTokenValid(jwt))
                .thenReturn(true);

        when(jwtService.extractUsername(jwt))
                .thenReturn(email);

        when(jwtService.extractUserId(jwt))
                .thenReturn(userId);

        when(jwtService.extractClaim(eq(jwt), any()))
                .thenReturn(UserStatus.ACTIVE);

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();

        assertThat(authentication.getPrincipal())
                .isInstanceOf(JwtPrincipal.class);

        JwtPrincipal principal =
                (JwtPrincipal) authentication.getPrincipal();

        assertThat(principal.id()).isEqualTo(userId);
        assertThat(principal.email()).isEqualTo(email);
        assertThat(principal.status()).isEqualTo(UserStatus.ACTIVE);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateInactiveUser()
            throws ServletException, IOException {

        UUID userId = UUID.randomUUID();
        String email = "john@example.com";
        String jwt = "valid-jwt";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.isTokenValid(jwt))
                .thenReturn(true);

        when(jwtService.extractUsername(jwt))
                .thenReturn(email);

        when(jwtService.extractUserId(jwt))
                .thenReturn(userId);

        when(jwtService.extractClaim(eq(jwt), any()))
                .thenReturn(UserStatus.INACTIVE);

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic some-token");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws ServletException, IOException {

        String jwt = "invalid-jwt";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.isTokenValid(jwt))
                .thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);

        verify(jwtService).isTokenValid(jwt);
        verify(jwtService, never()).extractUsername(jwt);
        verify(jwtService, never()).extractUserId(jwt);
    }

    @Test
    void shouldClearSecurityContextWhenTokenProcessingFails()
            throws ServletException, IOException {

        String jwt = "malformed-jwt";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.isTokenValid(jwt))
                .thenThrow(new RuntimeException("Invalid JWT"));

        SecurityContextHolder.getContext().setAuthentication(
                mock(Authentication.class)
        );

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);
    }
}