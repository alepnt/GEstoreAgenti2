package com.example.common.command;

import com.example.common.dto.InvoiceDTO;

import java.util.Collection;

/**
 * Restituisce tutte le fatture presenti nel contesto.
 */
public class ListInvoicesCommand implements Command<Collection<InvoiceDTO>> {

    @Override
    public Collection<InvoiceDTO> execute(CommandContext context) {
        return context.invoiceValues();
    }
}
