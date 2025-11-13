package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Comando per la creazione di una fattura.
 */
public class CreateInvoiceCommand implements ClientCommand<InvoiceDTO> {

    private final InvoiceDTO invoice;

    public CreateInvoiceCommand(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) {
        InvoiceDTO created = gateway.createInvoice(invoice);
        List<DocumentHistoryDTO> history = gateway.invoiceHistory(created.getId());
        return CommandResult.withHistory(created, created.getId(), DocumentType.INVOICE, history);
    }

    @Override
    public String description() {
        return "Creazione fattura per " + invoice.getCustomerName();
    }
}
