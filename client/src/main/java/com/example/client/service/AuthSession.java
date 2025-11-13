package com.example.client.service;

import java.time.Instant;

public record AuthSession(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserSummary user
) {
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}
