package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.CustomerDTO;

/**
 * Aggiorna i dati di un cliente.
 */
public class UpdateCustomerCommand implements ClientCommand<CustomerDTO> {

    private final Long id;
    private final CustomerDTO customer;

    public UpdateCustomerCommand(Long id, CustomerDTO customer) {
        this.id = id;
        this.customer = customer;
    }

    @Override
    public CommandResult<CustomerDTO> execute(BackendGateway gateway) {
        CustomerDTO updated = gateway.updateCustomer(id, customer);
        return CommandResult.withoutHistory(updated);
    }

    @Override
    public String description() {
        return "Aggiornamento cliente #" + id;
    }
}
