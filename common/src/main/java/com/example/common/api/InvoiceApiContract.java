package com.example.common.api;

import com.example.common.dto.InvoiceDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso fra client e server per la gestione delle fatture.
 */
public interface InvoiceApiContract {

    List<InvoiceDTO> listInvoices();

    Optional<InvoiceDTO> findById(String id);

    InvoiceDTO create(InvoiceDTO invoiceDTO);

    InvoiceDTO update(String id, InvoiceDTO invoiceDTO);

    void delete(String id);
}
