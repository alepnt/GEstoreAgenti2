package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Aggiorna un contratto esistente.
 */
public class UpdateContractCommand implements ClientCommand<ContractDTO> {

    private final Long id;
    private final ContractDTO contract;

    public UpdateContractCommand(Long id, ContractDTO contract) {
        this.id = id;
        this.contract = contract;
    }

    @Override
    public CommandResult<ContractDTO> execute(BackendGateway gateway) {
        ContractDTO updated = gateway.updateContract(id, contract);
        List<DocumentHistoryDTO> history = gateway.contractHistory(updated.getId());
        return CommandResult.withHistory(updated, updated.getId(), DocumentType.CONTRACT, history);
    }

    @Override
    public String description() {
        return "Aggiornamento contratto #" + id;
    }
}
