package com.example.client.service;

public record UserSummary(
        Long id,
        String email,
        String displayName,
        String azureId,
        Long roleId,
        Long teamId
) {
}
