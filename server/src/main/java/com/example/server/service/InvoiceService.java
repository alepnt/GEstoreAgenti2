package com.example.server.service;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Invoice;
import com.example.server.repository.InvoiceRepository;
import com.example.server.service.mapper.DocumentHistoryMapper;
import com.example.server.service.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final DocumentHistoryService documentHistoryService;
    private final CommissionService commissionService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          DocumentHistoryService documentHistoryService,
                          CommissionService commissionService) {
        this.invoiceRepository = invoiceRepository;
        this.documentHistoryService = documentHistoryService;
        this.commissionService = commissionService;
    }

    public List<InvoiceDTO> findAll() {
        return invoiceRepository.findAllByOrderByIssueDateDesc().stream()
                .map(InvoiceMapper::toDto)
                .toList();
    }

    public Optional<InvoiceDTO> findById(Long id) {
        return invoiceRepository.findById(id).map(InvoiceMapper::toDto);
    }

    public InvoiceDTO create(InvoiceDTO dto) {
        Invoice source = InvoiceMapper.fromDto(dto);
        String number = StringUtils.hasText(source.getNumber()) ? source.getNumber() : generateNumber();
        Invoice invoice = new Invoice(
                null,
                source.getContractId(),
                number,
                source.getCustomerName(),
                source.getAmount(),
                source.getIssueDate(),
                source.getDueDate(),
                source.getStatus() != null ? source.getStatus() : InvoiceStatus.DRAFT,
                source.getPaymentDate(),
                source.getNotes(),
                null,
                null
        );
        Invoice saved = invoiceRepository.save(invoice);
        documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.CREATED, "Fattura creata: " + saved.getNumber());
        return InvoiceMapper.toDto(saved);
    }

    public Optional<InvoiceDTO> update(Long id, InvoiceDTO dto) {
        return invoiceRepository.findById(id)
                .map(existing -> {
                    Invoice updated = existing.updateFrom(InvoiceMapper.fromDto(dto));
                    Invoice saved = invoiceRepository.save(updated);
                    documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.UPDATED, "Fattura aggiornata");
                    if (existing.getStatus() != saved.getStatus()) {
                        documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.STATUS_CHANGED,
                                "Stato cambiato da " + existing.getStatus() + " a " + saved.getStatus());
                    }
                    return InvoiceMapper.toDto(saved);
                });
    }

    public boolean delete(Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoiceRepository.deleteById(id);
                    documentHistoryService.log(DocumentType.INVOICE, invoice.getId(), DocumentAction.DELETED, "Fattura eliminata");
                    return true;
                })
                .orElse(false);
    }

    public Optional<InvoiceDTO> registerPayment(Long id, InvoicePaymentRequest paymentRequest) {
        LocalDate paymentDate = paymentRequest.getPaymentDate() != null ? paymentRequest.getPaymentDate() : LocalDate.now();
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    Invoice saved = invoiceRepository.save(invoice.registerPayment(paymentDate, InvoiceStatus.PAID));
                    documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.PAYMENT_REGISTERED,
                            "Pagamento registrato il " + paymentDate);
                    commissionService.updateAfterPayment(saved.getContractId(), saved.getAmount(),
                            paymentRequest.getAmountPaid() != null ? paymentRequest.getAmountPaid() : saved.getAmount());
                    return InvoiceMapper.toDto(saved);
                });
    }

    public List<DocumentHistoryDTO> history(Long id) {
        return documentHistoryService.list(DocumentType.INVOICE, id).stream()
                .map(DocumentHistoryMapper::toDto)
                .toList();
    }

    private String generateNumber() {
        long timestamp = System.currentTimeMillis();
        return "INV-" + timestamp;
    }
}
