package com.example.client.command;

import com.example.client.service.BackendGateway;

/**
 * Esegue i comandi e registra i relativi memento nello storico.
 */
public class CommandExecutor {

    private final BackendGateway backendGateway;
    private final CommandHistoryCaretaker caretaker;

    public CommandExecutor(BackendGateway backendGateway, CommandHistoryCaretaker caretaker) {
        this.backendGateway = backendGateway;
        this.caretaker = caretaker;
    }

    public <T> CommandResult<T> execute(ClientCommand<T> command) {
        CommandResult<T> result = command.execute(backendGateway);
        if (result != null) {
            caretaker.addMemento(new CommandMemento(command.description(), result));
        }
        return result;
    }
}
