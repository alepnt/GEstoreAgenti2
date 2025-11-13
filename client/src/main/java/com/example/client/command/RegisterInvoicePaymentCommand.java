package com.example.client.command;

import com.example.client.service.BackendGateway;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.enums.DocumentType;

import java.util.List;

/**
 * Comando per registrare il pagamento di una fattura.
 */
public class RegisterInvoicePaymentCommand implements ClientCommand<InvoiceDTO> {

    private final Long id;
    private final InvoicePaymentRequest paymentRequest;

    public RegisterInvoicePaymentCommand(Long id, InvoicePaymentRequest paymentRequest) {
        this.id = id;
        this.paymentRequest = paymentRequest;
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) {
        InvoiceDTO updated = gateway.registerInvoicePayment(id, paymentRequest);
        List<DocumentHistoryDTO> history = gateway.invoiceHistory(id);
        return CommandResult.withHistory(updated, id, DocumentType.INVOICE, history);
    }

    @Override
    public String description() {
        return "Pagamento fattura #" + id;
    }
}
