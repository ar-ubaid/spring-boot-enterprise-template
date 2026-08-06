@NullMarked
package io.github.arubaid.user.auth.principal;

/*
 * Authentication principal flow:
 *
 * UserService
 *     │
 *     └── AuthPrincipal ──→ AuthenticationManager
 *                              │
 *                              └── username/password authentication
 * JWT
 *     │
 *     └── JwtAuthenticationFilter ──→ JwtPrincipal ──→ SecurityContext
 *
 * JwtPrincipal does not contain a password because it represents an
 * already-authenticated identity reconstructed from a validated JWT.
 * The password is required only during the initial authentication flow.
 */

import org.jspecify.annotations.NullMarked;