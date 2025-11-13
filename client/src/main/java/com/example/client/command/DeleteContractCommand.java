package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Elimina un contratto.
 */
public class DeleteContractCommand implements ClientCommand<Void> {

    private final Long id;

    public DeleteContractCommand(Long id) {
        this.id = id;
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) {
        gateway.deleteContract(id);
        List<DocumentHistoryDTO> history = gateway.contractHistory(id);
        return CommandResult.withHistory(null, id, DocumentType.CONTRACT, history);
    }

    @Override
    public String description() {
        return "Eliminazione contratto #" + id;
    }
}
