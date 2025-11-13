package com.example.common.command;

import com.example.common.dto.InvoiceDTO;

import java.util.Optional;

/**
 * Cancella una fattura esistente dal contesto.
 */
public class DeleteInvoiceCommand implements Command<Optional<InvoiceDTO>> {

    private final Long id;

    public DeleteInvoiceCommand(Long id) {
        this.id = id;
    }

    @Override
    public Optional<InvoiceDTO> execute(CommandContext context) {
        return Optional.ofNullable(context.getInvoices().remove(id));
    }
}
