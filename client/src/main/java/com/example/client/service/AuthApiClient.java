package com.example.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public AuthApiClient() {
        this("http://localhost:8080");
    }

    public AuthApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public AuthSession login(LoginForm form) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(serialize(new LoginPayload(form.accessToken(), form.email(), form.displayName(), form.azureId()))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        AuthResponsePayload payload = deserialize(response.body(), AuthResponsePayload.class);
        return new AuthSession(payload.accessToken(), payload.tokenType(), payload.expiresAt(), payload.user());
    }

    public UserSummary register(RegisterForm form) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(serialize(new RegisterPayload(form.azureId(), form.email(), form.displayName(), form.agentCode(), form.password(), form.teamName(), form.roleName()))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return deserialize(response.body(), UserSummary.class);
    }

    private void ensureSuccess(HttpResponse<String> response) throws IOException {
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Errore nella risposta del server: " + status + " - " + response.body());
        }
    }

    private String serialize(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    private <T> T deserialize(String body, Class<T> type) throws JsonProcessingException {
        return mapper.readValue(body, type);
    }

    private record LoginPayload(String accessToken, String email, String displayName, String azureId) {
    }

    private record RegisterPayload(String azureId, String email, String displayName, String agentCode, String password, String teamName, String roleName) {
    }

    private record AuthResponsePayload(String accessToken, String tokenType, java.time.Instant expiresAt, UserSummary user) {
    }
}
