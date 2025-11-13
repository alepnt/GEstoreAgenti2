package com.example.client.session;

import com.example.client.service.AuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SessionStore {

    private final ObjectMapper mapper;
    private final Path sessionFile;

    public SessionStore() {
        this(Path.of(System.getProperty("user.home"), ".gestore-agenti-session.json"));
    }

    public SessionStore(Path sessionFile) {
        this.sessionFile = sessionFile;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public Optional<AuthSession> load() {
        if (!Files.exists(sessionFile)) {
            return Optional.empty();
        }
        try {
            byte[] content = Files.readAllBytes(sessionFile);
            AuthSession session = mapper.readValue(content, AuthSession.class);
            return Optional.ofNullable(session);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void save(AuthSession session) throws IOException {
        if (sessionFile.getParent() != null) {
            Files.createDirectories(sessionFile.getParent());
        }
        byte[] payload = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(session);
        Files.write(sessionFile, payload);
    }

    public void clear() throws IOException {
        Files.deleteIfExists(sessionFile);
    }

    public Path getSessionFile() {
        return sessionFile;
    }
}
