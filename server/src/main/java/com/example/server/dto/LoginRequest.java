package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String accessToken,
        @NotBlank String email,
        @NotBlank String displayName,
        @NotBlank String azureId
) {
}
