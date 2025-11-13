package com.example.server.dto;

public record UserSummary(
        Long id,
        String email,
        String displayName,
        String azureId,
        Long roleId,
        Long teamId
) {
}
