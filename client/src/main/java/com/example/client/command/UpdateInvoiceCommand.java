package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Comando per aggiornare una fattura esistente.
 */
public class UpdateInvoiceCommand implements ClientCommand<InvoiceDTO> {

    private final Long id;
    private final InvoiceDTO invoice;

    public UpdateInvoiceCommand(Long id, InvoiceDTO invoice) {
        this.id = id;
        this.invoice = invoice;
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) {
        InvoiceDTO updated = gateway.updateInvoice(id, invoice);
        List<DocumentHistoryDTO> history = gateway.invoiceHistory(updated.getId());
        return CommandResult.withHistory(updated, updated.getId(), DocumentType.INVOICE, history);
    }

    @Override
    public String description() {
        return "Aggiornamento fattura #" + id;
    }
}
