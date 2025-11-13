package com.example.common.command;

import com.example.common.dto.InvoiceDTO;

import java.util.Objects;
import java.util.Optional;

/**
 * Aggiorna una fattura esistente.
 */
public class UpdateInvoiceCommand implements Command<Optional<InvoiceDTO>> {

    private final String id;
    private final InvoiceDTO invoice;

    public UpdateInvoiceCommand(String id, InvoiceDTO invoice) {
        this.id = Objects.requireNonNull(id, "id");
        this.invoice = Objects.requireNonNull(invoice, "invoice");
    }

    @Override
    public Optional<InvoiceDTO> execute(CommandContext context) {
        return Optional.ofNullable(context.getInvoices().computeIfPresent(id, (key, existing) -> invoice));
    }
}
