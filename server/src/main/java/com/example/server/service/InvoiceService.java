package com.example.server.service;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Invoice;
import com.example.server.domain.InvoiceLine;
import com.example.server.repository.InvoiceLineRepository;
import com.example.server.repository.InvoiceRepository;
import com.example.server.service.mapper.DocumentHistoryMapper;
import com.example.server.service.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final CustomerService customerService;
    private final ArticleService articleService;
    private final DocumentHistoryService documentHistoryService;
    private final CommissionService commissionService;
    private final StatisticsService statisticsService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceLineRepository invoiceLineRepository,
                          CustomerService customerService,
                          ArticleService articleService,
                          DocumentHistoryService documentHistoryService,
                          CommissionService commissionService,
                          StatisticsService statisticsService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.customerService = customerService;
        this.articleService = articleService;
        this.documentHistoryService = documentHistoryService;
        this.commissionService = commissionService;
        this.statisticsService = statisticsService;
    }

    public List<InvoiceDTO> findAll() {
        return invoiceRepository.findAllByOrderByIssueDateDesc().stream()
                .map(invoice -> InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoice.getId())))
                .toList();
    }

    public Optional<InvoiceDTO> findById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoice.getId())));
    }

    @Transactional
    public InvoiceDTO create(InvoiceDTO dto) {
        Invoice source = InvoiceMapper.fromDto(dto);
        if (source.getCustomerId() == null) {
            throw new IllegalArgumentException("Il cliente è obbligatorio");
        }
        String customerName = customerService.require(source.getCustomerId()).getName();
        List<InvoiceLine> lines = prepareInvoiceLines(dto.getLines());
        BigDecimal amount = determineAmount(source.getAmount(), lines);
        String number = StringUtils.hasText(source.getNumber()) ? source.getNumber() : generateNumber();
        Invoice invoice = new Invoice(
                null,
                source.getContractId(),
                number,
                source.getCustomerId(),
                customerName,
                amount,
                source.getIssueDate(),
                source.getDueDate(),
                source.getStatus() != null ? source.getStatus() : InvoiceStatus.DRAFT,
                source.getPaymentDate(),
                source.getNotes(),
                null,
                null
        );
        Invoice saved = invoiceRepository.save(invoice);
        replaceInvoiceLines(saved.getId(), lines);
        documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.CREATED, "Fattura creata: " + saved.getNumber());
        statisticsService.clearCache();
        return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(saved.getId()));
    }

    @Transactional
    public Optional<InvoiceDTO> update(Long id, InvoiceDTO dto) {
        return invoiceRepository.findById(id)
                .map(existing -> {
                    Invoice updatedSource = InvoiceMapper.fromDto(dto);
                    if (updatedSource.getCustomerId() == null) {
                        throw new IllegalArgumentException("Il cliente è obbligatorio");
                    }
                    String customerName = customerService.require(updatedSource.getCustomerId()).getName();
                    List<InvoiceLine> lines = prepareInvoiceLines(dto.getLines());
                    BigDecimal amount = determineAmount(updatedSource.getAmount(), lines);
                    Invoice updated = new Invoice(
                            existing.getId(),
                            updatedSource.getContractId(),
                            StringUtils.hasText(updatedSource.getNumber()) ? updatedSource.getNumber() : existing.getNumber(),
                            updatedSource.getCustomerId(),
                            customerName,
                            amount,
                            updatedSource.getIssueDate(),
                            updatedSource.getDueDate(),
                            updatedSource.getStatus() != null ? updatedSource.getStatus() : existing.getStatus(),
                            updatedSource.getPaymentDate(),
                            updatedSource.getNotes(),
                            existing.getCreatedAt(),
                            existing.getUpdatedAt()
                    );
                    Invoice saved = invoiceRepository.save(updated);
                    replaceInvoiceLines(saved.getId(), lines);
                    documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.UPDATED, "Fattura aggiornata");
                    if (existing.getStatus() != saved.getStatus()) {
                        documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.STATUS_CHANGED,
                                "Stato cambiato da " + existing.getStatus() + " a " + saved.getStatus());
                    }
                    statisticsService.clearCache();
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(saved.getId()));
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoiceRepository.deleteById(id);
                    invoiceLineRepository.deleteByInvoiceId(id);
                    documentHistoryService.log(DocumentType.INVOICE, invoice.getId(), DocumentAction.DELETED, "Fattura eliminata");
                    statisticsService.clearCache();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<InvoiceDTO> registerPayment(Long id, InvoicePaymentRequest paymentRequest) {
        LocalDate paymentDate = paymentRequest.getPaymentDate() != null ? paymentRequest.getPaymentDate() : LocalDate.now();
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    Invoice saved = invoiceRepository.save(invoice.registerPayment(paymentDate, InvoiceStatus.PAID));
                    documentHistoryService.log(DocumentType.INVOICE, saved.getId(), DocumentAction.PAYMENT_REGISTERED,
                            "Pagamento registrato il " + paymentDate);
                    commissionService.updateAfterPayment(saved.getContractId(), saved.getAmount(),
                            paymentRequest.getAmountPaid() != null ? paymentRequest.getAmountPaid() : saved.getAmount());
                    statisticsService.clearCache();
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(saved.getId()));
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

    private List<InvoiceLine> prepareInvoiceLines(List<InvoiceLineDTO> lineDTOs) {
        if (lineDTOs == null || lineDTOs.isEmpty()) {
            return List.of();
        }
        List<InvoiceLine> lines = new ArrayList<>();
        for (InvoiceLineDTO dto : lineDTOs) {
            if (dto == null) {
                continue;
            }
            var article = dto.getArticleId() != null ? articleService.require(dto.getArticleId()) : null;
            BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : BigDecimal.ONE;
            if (quantity.signum() <= 0) {
                throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
            }
            BigDecimal unitPrice = dto.getUnitPrice() != null
                    ? dto.getUnitPrice()
                    : (article != null ? article.getUnitPrice() : BigDecimal.ZERO);
            if (unitPrice.signum() < 0) {
                throw new IllegalArgumentException("Il prezzo unitario non può essere negativo");
            }
            BigDecimal vatRate = dto.getVatRate() != null
                    ? dto.getVatRate()
                    : (article != null ? article.getVatRate() : BigDecimal.ZERO);
            String description = StringUtils.hasText(dto.getDescription())
                    ? dto.getDescription().trim()
                    : (article != null ? article.getName() : null);
            String articleCode = article != null ? article.getCode() : dto.getArticleCode();
            BigDecimal total = calculateTotal(quantity, unitPrice, vatRate);
            lines.add(new InvoiceLine(
                    dto.getId(),
                    null,
                    article != null ? article.getId() : dto.getArticleId(),
                    articleCode,
                    description,
                    quantity,
                    unitPrice,
                    vatRate,
                    total
            ));
        }
        return lines;
    }

    private BigDecimal determineAmount(BigDecimal requestedAmount, List<InvoiceLine> lines) {
        if (lines != null && !lines.isEmpty()) {
            return lines.stream()
                    .map(InvoiceLine::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return requestedAmount != null ? requestedAmount : BigDecimal.ZERO;
    }

    private void replaceInvoiceLines(Long invoiceId, List<InvoiceLine> lines) {
        invoiceLineRepository.deleteByInvoiceId(invoiceId);
        if (lines == null || lines.isEmpty()) {
            return;
        }
        for (InvoiceLine line : lines) {
            invoiceLineRepository.save(line.withInvoice(invoiceId));
        }
    }

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal unitPrice, BigDecimal vatRate) {
        BigDecimal subtotal = unitPrice.multiply(quantity);
        if (vatRate == null) {
            return subtotal;
        }
        return subtotal.add(subtotal.multiply(vatRate));
    }
}
