package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Comando per eliminare una fattura.
 */
public class DeleteInvoiceCommand implements ClientCommand<Void> {

    private final Long id;

    public DeleteInvoiceCommand(Long id) {
        this.id = id;
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) {
        gateway.deleteInvoice(id);
        List<DocumentHistoryDTO> history = gateway.invoiceHistory(id);
        return CommandResult.withHistory(null, id, DocumentType.INVOICE, history);
    }

    @Override
    public String description() {
        return "Eliminazione fattura #" + id;
    }
}
