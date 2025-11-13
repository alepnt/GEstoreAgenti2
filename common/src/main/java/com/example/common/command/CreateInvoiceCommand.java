package com.example.common.command;

import com.example.common.dto.InvoiceDTO;

import java.util.Objects;

/**
 * Comando per la creazione di una fattura.
 */
public class CreateInvoiceCommand implements Command<InvoiceDTO> {

    private final InvoiceDTO invoice;

    public CreateInvoiceCommand(InvoiceDTO invoice) {
        this.invoice = Objects.requireNonNull(invoice, "invoice");
    }

    @Override
    public InvoiceDTO execute(CommandContext context) {
        if (invoice.getId() == null) {
            invoice.setId(context.nextInvoiceId());
        }
        context.getInvoices().put(invoice.getId(), invoice);
        return invoice;
    }
}
