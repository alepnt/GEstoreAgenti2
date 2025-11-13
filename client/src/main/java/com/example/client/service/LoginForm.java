package com.example.client.service;

public record LoginForm(
        String accessToken,
        String email,
        String displayName,
        String azureId
) {
}
