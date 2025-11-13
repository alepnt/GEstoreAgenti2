package com.example.common.command;

import java.util.Objects;

/**
 * Semplice dispatcher per l'esecuzione dei comandi condiviso fra moduli.
 */
public class CommandBus {

    private final CommandContext context;

    public CommandBus(CommandContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    public <R> R dispatch(Command<R> command) {
        return command.execute(context);
    }
}
