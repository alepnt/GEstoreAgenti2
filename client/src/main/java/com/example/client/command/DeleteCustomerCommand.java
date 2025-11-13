package com.example.client.command;

import com.example.client.service.BackendGateway;

/**
 * Elimina un cliente dall'anagrafica.
 */
public class DeleteCustomerCommand implements ClientCommand<Void> {

    private final Long id;

    public DeleteCustomerCommand(Long id) {
        this.id = id;
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) {
        gateway.deleteCustomer(id);
        return CommandResult.withoutHistory(null);
    }

    @Override
    public String description() {
        return "Eliminazione cliente #" + id;
    }
}
