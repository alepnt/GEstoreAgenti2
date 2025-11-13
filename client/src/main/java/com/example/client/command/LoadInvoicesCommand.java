package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.InvoiceDTO;

import java.util.List;

/**
 * Comando di caricamento delle fatture dal backend.
 */
public class LoadInvoicesCommand implements ClientCommand<List<InvoiceDTO>> {

    @Override
    public CommandResult<List<InvoiceDTO>> execute(BackendGateway gateway) {
        return CommandResult.withoutHistory(gateway.listInvoices());
    }

    @Override
    public String description() {
        return "Caricamento fatture";
    }
}
