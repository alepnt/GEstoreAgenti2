package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Objects;

/**
 * Comando per la creazione di un contratto.
 */
public class CreateContractCommand implements Command<ContractDTO> {

    private final ContractDTO contract;

    public CreateContractCommand(ContractDTO contract) {
        this.contract = Objects.requireNonNull(contract, "contract");
    }

    @Override
    public ContractDTO execute(CommandContext context) {
        context.getContracts().put(contract.getId(), contract);
        return contract;
    }
}
