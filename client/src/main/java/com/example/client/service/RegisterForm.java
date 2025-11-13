package com.example.client.service;

public record RegisterForm(
        String azureId,
        String email,
        String displayName,
        String agentCode,
        String password,
        String teamName,
        String roleName
) {
}
