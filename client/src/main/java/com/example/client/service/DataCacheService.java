package com.example.client.service;

import com.example.common.command.CommandBus;
import com.example.common.command.CommandContext;
import com.example.common.command.CreateContractCommand;
import com.example.common.command.CreateInvoiceCommand;
import com.example.common.command.ListContractsCommand;
import com.example.common.command.ListInvoicesCommand;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

/**
 * Servizio locale che riutilizza i comandi condivisi per mantenere una cache dei dati.
 */
public class DataCacheService {

    private final CommandContext context = new CommandContext();
    private final CommandBus commandBus = new CommandBus(context);

    public DataCacheService() {
        seedData();
    }

    public Collection<InvoiceDTO> getInvoices() {
        return commandBus.dispatch(new ListInvoicesCommand());
    }

    public Collection<ContractDTO> getContracts() {
        return commandBus.dispatch(new ListContractsCommand());
    }

    public InvoiceDTO saveInvoice(InvoiceDTO invoiceDTO) {
        if (invoiceDTO.getId() == null) {
            invoiceDTO.setId(UUID.randomUUID().toString());
        }
        return commandBus.dispatch(new CreateInvoiceCommand(invoiceDTO));
    }

    public ContractDTO saveContract(ContractDTO contractDTO) {
        if (contractDTO.getId() == null) {
            contractDTO.setId(UUID.randomUUID().toString());
        }
        return commandBus.dispatch(new CreateContractCommand(contractDTO));
    }

    private void seedData() {
        saveInvoice(new InvoiceDTO(UUID.randomUUID().toString(), "CUST-001", new BigDecimal("1200.00"), LocalDate.now().minusDays(10), InvoiceStatus.DRAFT));
        saveInvoice(new InvoiceDTO(UUID.randomUUID().toString(), "CUST-002", new BigDecimal("800.50"), LocalDate.now().minusDays(3), InvoiceStatus.SENT));
        saveContract(new ContractDTO(UUID.randomUUID().toString(), "AG-001", "Contratto di consulenza", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10)));
        saveContract(new ContractDTO(UUID.randomUUID().toString(), "AG-002", "Contratto di vendita", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5)));
    }
}
