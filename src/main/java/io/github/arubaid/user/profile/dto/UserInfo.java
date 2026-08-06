package io.github.arubaid.user.profile.dto;

import io.github.arubaid.user.profile.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private UUID id;
    private String email;
    private UserStatus status;
}