package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Collection;

/**
 * Restituisce tutti i contratti presenti nel contesto.
 */
public class ListContractsCommand implements Command<Collection<ContractDTO>> {

    @Override
    public Collection<ContractDTO> execute(CommandContext context) {
        return context.contractValues();
    }
}
