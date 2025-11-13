package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.CustomerDTO;

/**
 * Crea un nuovo cliente nell'anagrafica.
 */
public class CreateCustomerCommand implements ClientCommand<CustomerDTO> {

    private final CustomerDTO customer;

    public CreateCustomerCommand(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public CommandResult<CustomerDTO> execute(BackendGateway gateway) {
        CustomerDTO created = gateway.createCustomer(customer);
        return CommandResult.withoutHistory(created);
    }

    @Override
    public String description() {
        return "Creazione cliente " + customer.getName();
    }
}
