package io.github.arubaid.user.auth.config;

import io.github.arubaid.user.auth.principal.JwtPrincipal;
import io.github.arubaid.user.auth.service.JwtService;
import io.github.arubaid.user.profile.entity.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            if (jwtService.isTokenValid(jwt)) {
                userEmail = jwtService.extractUsername(jwt);
                UUID userId = jwtService.extractUserId(jwt);
                UserStatus status = jwtService.extractClaim(jwt, claims -> UserStatus.valueOf(claims.get("status", String.class)));

                if (status == UserStatus.ACTIVE && userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Build principal representing the authenticated identity
                    JwtPrincipal jwtPrincipal = new JwtPrincipal(userId, userEmail, status);
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            jwtPrincipal,
                            null,
                            jwtPrincipal.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token parsing failed (expired or altered signature)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
