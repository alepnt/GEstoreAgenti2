package com.example.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String azureId,
        @NotBlank @Email String email,
        @NotBlank String displayName,
        @Size(min = 6, max = 64) String agentCode,
        @Size(min = 8, max = 128) String password,
        String teamName,
        String roleName
) {
}
