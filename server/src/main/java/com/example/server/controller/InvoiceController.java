package com.example.server.controller;

import com.example.common.api.InvoiceApiContract;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.server.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
        return invoiceService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<InvoiceDTO> findById(@PathVariable Long id) {
        return invoiceService.findById(id);
    }

    @Override
    @PostMapping
    public InvoiceDTO create(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.create(invoiceDTO);
    }

    @Override
    @PutMapping("/{id}")
    public InvoiceDTO update(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.update(id, invoiceDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = invoiceService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
        }
    }

    @Override
    @PostMapping("/{id}/payments")
    public InvoiceDTO registerPayment(@PathVariable Long id, @RequestBody InvoicePaymentRequest paymentRequest) {
        return invoiceService.registerPayment(id, paymentRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    @Override
    @GetMapping("/{id}/history")
    public List<DocumentHistoryDTO> history(@PathVariable Long id) {
        return invoiceService.history(id);
    }

}
