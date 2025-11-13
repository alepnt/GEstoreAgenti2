package com.example.client.command;

import java.time.Instant;

/**
 * Memento che conserva il risultato di un comando eseguito.
 */
public class CommandMemento {

    private final String description;
    private final Instant executedAt;
    private final CommandResult<?> result;

    public CommandMemento(String description, CommandResult<?> result) {
        this.description = description;
        this.result = result;
        this.executedAt = Instant.now();
    }

    public String getDescription() {
        return description;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public CommandResult<?> getResult() {
        return result;
    }
}
