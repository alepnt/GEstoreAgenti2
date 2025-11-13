package com.example.common.command;

import com.example.common.dto.ContractDTO;
import com.example.common.dto.InvoiceDTO;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contesto generico per l'esecuzione dei comandi CRUD.
 */
public class CommandContext {

    private final Map<String, InvoiceDTO> invoices = new ConcurrentHashMap<>();
    private final Map<String, ContractDTO> contracts = new ConcurrentHashMap<>();

    public Map<String, InvoiceDTO> getInvoices() {
        return invoices;
    }

    public Map<String, ContractDTO> getContracts() {
        return contracts;
    }

    public Collection<InvoiceDTO> invoiceValues() {
        return invoices.values();
    }

    public Collection<ContractDTO> contractValues() {
        return contracts.values();
    }
}
