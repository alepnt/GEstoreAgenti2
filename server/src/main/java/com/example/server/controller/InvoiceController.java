package com.example.server.controller;

import com.example.common.api.InvoiceApiContract;
import com.example.common.dto.InvoiceDTO;
import com.example.server.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Esempio di controller REST conforme al contratto condiviso.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController implements InvoiceApiContract {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    @GetMapping
    public List<InvoiceDTO> listInvoices() {
        Collection<InvoiceDTO> invoices = invoiceService.listInvoices();
        return List.copyOf(invoices);
    }

    @Override
    @GetMapping("/{id}")
    public Optional<InvoiceDTO> findById(@PathVariable String id) {
        return invoiceService.listInvoices().stream()
                .filter(invoice -> invoice.getId().equals(id))
                .findFirst();
    }

    @Override
    @PostMapping
    public InvoiceDTO create(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.create(invoiceDTO);
    }

    @Override
    @PutMapping("/{id}")
    public InvoiceDTO update(@PathVariable String id, @RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.update(id, invoiceDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        invoiceService.delete(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
    }

    @GetMapping("/{id}/response")
    public ResponseEntity<InvoiceDTO> getInvoiceResponse(@PathVariable String id) {
        return findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
