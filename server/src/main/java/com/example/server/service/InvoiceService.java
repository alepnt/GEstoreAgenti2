package com.example.server.service;

import com.example.common.command.CommandBus;
import com.example.common.command.CreateInvoiceCommand;
import com.example.common.command.DeleteInvoiceCommand;
import com.example.common.command.ListInvoicesCommand;
import com.example.common.command.UpdateInvoiceCommand;
import com.example.common.dto.InvoiceDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Servizio che incapsula l'utilizzo del Command pattern per le fatture.
 */
@Service
public class InvoiceService {

    private final CommandBus commandBus;

    public InvoiceService(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    public Collection<InvoiceDTO> listInvoices() {
        return commandBus.dispatch(new ListInvoicesCommand());
    }

    public InvoiceDTO create(InvoiceDTO invoiceDTO) {
        return commandBus.dispatch(new CreateInvoiceCommand(invoiceDTO));
    }

    public Optional<InvoiceDTO> update(String id, InvoiceDTO invoiceDTO) {
        return commandBus.dispatch(new UpdateInvoiceCommand(id, invoiceDTO));
    }

    public Optional<InvoiceDTO> delete(String id) {
        return commandBus.dispatch(new DeleteInvoiceCommand(id));
    }
}
