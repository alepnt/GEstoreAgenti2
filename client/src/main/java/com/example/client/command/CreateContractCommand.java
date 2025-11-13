package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Crea un nuovo contratto commerciale.
 */
public class CreateContractCommand implements ClientCommand<ContractDTO> {

    private final ContractDTO contract;

    public CreateContractCommand(ContractDTO contract) {
        this.contract = contract;
    }

    @Override
    public CommandResult<ContractDTO> execute(BackendGateway gateway) {
        ContractDTO created = gateway.createContract(contract);
        List<DocumentHistoryDTO> history = gateway.contractHistory(created.getId());
        return CommandResult.withHistory(created, created.getId(), DocumentType.CONTRACT, history);
    }

    @Override
    public String description() {
        return "Creazione contratto per " + contract.getCustomerName();
    }
}
