package io.github.arubaid.user.profile.controller;

import io.github.arubaid.common.exception.FeatureNotImplementedException;
import io.github.arubaid.user.auth.principal.JwtPrincipal;
import io.github.arubaid.user.profile.dto.UserInfo;
import io.github.arubaid.user.profile.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public void all() {
        throw new FeatureNotImplementedException("all");
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {

        UserInfo userInfo = userService.getCurrentUser(jwtPrincipal.id());

        if (userInfo.getStatus() != jwtPrincipal.status()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userInfo);
    }
}