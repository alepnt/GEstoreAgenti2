package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ContractDTO;

import java.util.List;

/**
 * Carica i contratti dal backend.
 */
public class LoadContractsCommand implements ClientCommand<List<ContractDTO>> {

    @Override
    public CommandResult<List<ContractDTO>> execute(BackendGateway gateway) {
        return CommandResult.withoutHistory(gateway.listContracts());
    }

    @Override
    public String description() {
        return "Caricamento contratti";
    }
}
