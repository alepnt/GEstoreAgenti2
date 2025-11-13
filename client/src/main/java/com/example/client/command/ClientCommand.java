package com.example.client.command;

import com.example.client.service.BackendGateway;

/**
 * Interfaccia base del Command pattern lato client.
 */
public interface ClientCommand<T> {

    CommandResult<T> execute(BackendGateway gateway);

    String description();
}
