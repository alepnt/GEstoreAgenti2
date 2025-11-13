package com.example.common.api;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso fra client e server per la gestione delle fatture.
 */
public interface InvoiceApiContract {

    List<InvoiceDTO> listInvoices();

    Optional<InvoiceDTO> findById(Long id);

    InvoiceDTO create(InvoiceDTO invoiceDTO);

    InvoiceDTO update(Long id, InvoiceDTO invoiceDTO);

    void delete(Long id);

    InvoiceDTO registerPayment(Long id, InvoicePaymentRequest paymentRequest);

    List<DocumentHistoryDTO> history(Long id);
}
