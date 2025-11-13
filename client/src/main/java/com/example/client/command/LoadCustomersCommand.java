package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.CustomerDTO;

import java.util.List;

/**
 * Carica l'elenco dei clienti dal backend.
 */
public class LoadCustomersCommand implements ClientCommand<List<CustomerDTO>> {

    @Override
    public CommandResult<List<CustomerDTO>> execute(BackendGateway gateway) {
        return CommandResult.withoutHistory(gateway.listCustomers());
    }

    @Override
    public String description() {
        return "Caricamento anagrafica clienti";
    }
}
