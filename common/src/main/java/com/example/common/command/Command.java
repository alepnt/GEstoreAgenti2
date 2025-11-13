package com.example.common.command;

/**
 * Interfaccia base del pattern Command utilizzabile da client e server per le operazioni CRUD.
 */
public interface Command<R> {

    R execute(CommandContext context);
}
