package com.example.client.service;

import com.example.client.command.CommandExecutor;
import com.example.client.command.CommandHistoryCaretaker;
import com.example.client.command.CreateContractCommand;
import com.example.client.command.CreateInvoiceCommand;
import com.example.client.command.DeleteContractCommand;
import com.example.client.command.DeleteInvoiceCommand;
import com.example.client.command.LoadContractsCommand;
import com.example.client.command.LoadInvoicesCommand;
import com.example.client.command.RegisterInvoicePaymentCommand;
import com.example.client.command.UpdateContractCommand;
import com.example.client.command.UpdateInvoiceCommand;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;

import java.util.List;

/**
 * Servizio client che orchestra le operazioni CRUD tramite Command pattern.
 */
public class DataCacheService {

    private final BackendGateway backendGateway;
    private final CommandHistoryCaretaker caretaker = new CommandHistoryCaretaker();
    private final CommandExecutor executor;

    public DataCacheService() {
        this(new BackendGateway());
    }

    public DataCacheService(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;
        this.executor = new CommandExecutor(backendGateway, caretaker);
    }

    public List<InvoiceDTO> getInvoices() {
        return executor.execute(new LoadInvoicesCommand()).value();
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        return executor.execute(new CreateInvoiceCommand(invoiceDTO)).value();
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        return executor.execute(new UpdateInvoiceCommand(id, invoiceDTO)).value();
    }

    public void deleteInvoice(Long id) {
        executor.execute(new DeleteInvoiceCommand(id));
    }

    public InvoiceDTO registerPayment(Long id, InvoicePaymentRequest paymentRequest) {
        return executor.execute(new RegisterInvoicePaymentCommand(id, paymentRequest)).value();
    }

    public List<ContractDTO> getContracts() {
        return executor.execute(new LoadContractsCommand()).value();
    }

    public ContractDTO createContract(ContractDTO contractDTO) {
        return executor.execute(new CreateContractCommand(contractDTO)).value();
    }

    public ContractDTO updateContract(Long id, ContractDTO contractDTO) {
        return executor.execute(new UpdateContractCommand(id, contractDTO)).value();
    }

    public void deleteContract(Long id) {
        executor.execute(new DeleteContractCommand(id));
    }

    public List<DocumentHistoryDTO> getInvoiceHistory(Long id) {
        return backendGateway.invoiceHistory(id);
    }

    public List<DocumentHistoryDTO> getContractHistory(Long id) {
        return backendGateway.contractHistory(id);
    }

    public CommandHistoryCaretaker getCaretaker() {
        return caretaker;
    }
}
