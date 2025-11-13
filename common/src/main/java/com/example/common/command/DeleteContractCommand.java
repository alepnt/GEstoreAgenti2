package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Optional;

/**
 * Cancella un contratto esistente dal contesto.
 */
public class DeleteContractCommand implements Command<Optional<ContractDTO>> {

    private final String id;

    public DeleteContractCommand(String id) {
        this.id = id;
    }

    @Override
    public Optional<ContractDTO> execute(CommandContext context) {
        return Optional.ofNullable(context.getContracts().remove(id));
    }
}
