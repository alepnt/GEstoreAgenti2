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
import java.util.Objects;
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
                .map(invoice -> {
                    Long invoiceId = Objects.requireNonNull(invoice.getId(), "invoice id must not be null");
                    return InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoiceId));
                })
                .toList();
    }

    public Optional<InvoiceDTO> findById(Long id) {
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(invoice -> {
                    Long invoiceId = Objects.requireNonNull(invoice.getId(), "invoice id must not be null");
                    return InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoiceId));
                });
    }

    @Transactional
    public InvoiceDTO create(InvoiceDTO dto) {
        InvoiceDTO requiredDto = Objects.requireNonNull(dto, "invoice must not be null");
        Invoice source = Objects.requireNonNull(InvoiceMapper.fromDto(requiredDto), "mapped invoice must not be null");
        Long customerId = Objects.requireNonNull(source.getCustomerId(), "customerId must not be null");
        String customerName = customerService.require(customerId).getName();
        List<InvoiceLine> lines = prepareInvoiceLines(requiredDto.getLines());
        BigDecimal amount = determineAmount(source.getAmount(), lines);
        String number = StringUtils.hasText(source.getNumber()) ? source.getNumber() : generateNumber();
        Invoice invoice = new Invoice(
                null,
                source.getContractId(),
                number,
                customerId,
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
        Invoice saved = Objects.requireNonNull(invoiceRepository.save(invoice), "saved invoice must not be null");
        Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null");
        replaceInvoiceLines(savedId, lines);
        documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.CREATED,
                "Fattura creata: " + saved.getNumber());
        statisticsService.clearCache();
        return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId));
    }

    @Transactional
    public Optional<InvoiceDTO> update(Long id, InvoiceDTO dto) {
        InvoiceDTO requiredDto = Objects.requireNonNull(dto, "invoice must not be null");
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    Invoice updatedSource = Objects.requireNonNull(InvoiceMapper.fromDto(requiredDto),
                            "mapped invoice must not be null");
                    Long customerId = Objects.requireNonNull(updatedSource.getCustomerId(), "customerId must not be null");
                    String customerName = customerService.require(customerId).getName();
                    List<InvoiceLine> lines = prepareInvoiceLines(requiredDto.getLines());
                    BigDecimal amount = determineAmount(updatedSource.getAmount(), lines);
                    Invoice updated = new Invoice(
                            existing.getId(),
                            updatedSource.getContractId(),
                            StringUtils.hasText(updatedSource.getNumber()) ? updatedSource.getNumber() : existing.getNumber(),
                            customerId,
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
                    Invoice saved = Objects.requireNonNull(invoiceRepository.save(updated), "saved invoice must not be null");
                    Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null");
                    replaceInvoiceLines(savedId, lines);
                    documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.UPDATED, "Fattura aggiornata");
                    if (existing.getStatus() != saved.getStatus()) {
                        documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.STATUS_CHANGED,
                                "Stato cambiato da " + existing.getStatus() + " a " + saved.getStatus());
                    }
                    statisticsService.clearCache();
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId));
                });
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return invoiceRepository.findById(requiredId)
                .map(invoice -> {
                    invoiceRepository.deleteById(requiredId);
                    invoiceLineRepository.deleteByInvoiceId(requiredId);
                    documentHistoryService.log(DocumentType.INVOICE,
                            Objects.requireNonNull(invoice.getId(), "invoice id must not be null"),
                            DocumentAction.DELETED,
                            "Fattura eliminata");
                    statisticsService.clearCache();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<InvoiceDTO> registerPayment(Long id, InvoicePaymentRequest paymentRequest) {
        InvoicePaymentRequest requiredRequest = Objects.requireNonNull(paymentRequest,
                "paymentRequest must not be null");
        LocalDate paymentDate = requiredRequest.getPaymentDate() != null ? requiredRequest.getPaymentDate() : LocalDate.now();
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(invoice -> {
                    Invoice saved = Objects.requireNonNull(
                            invoiceRepository.save(invoice.registerPayment(paymentDate, InvoiceStatus.PAID)),
                            "saved invoice must not be null");
                    Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null");
                    documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.PAYMENT_REGISTERED,
                            "Pagamento registrato il " + paymentDate);
                    commissionService.updateAfterPayment(saved.getContractId(), saved.getAmount(),
                            requiredRequest.getAmountPaid() != null ? requiredRequest.getAmountPaid() : saved.getAmount());
                    statisticsService.clearCache();
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId));
                });
    }

    public List<DocumentHistoryDTO> history(Long id) {
        return documentHistoryService.list(DocumentType.INVOICE, Objects.requireNonNull(id, "id must not be null")).stream()
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
        Long requiredInvoiceId = Objects.requireNonNull(invoiceId, "invoiceId must not be null");
        invoiceLineRepository.deleteByInvoiceId(requiredInvoiceId);
        if (lines == null || lines.isEmpty()) {
            return;
        }
        for (InvoiceLine line : lines) {
            InvoiceLine toSave = Objects.requireNonNull(line, "invoice line must not be null")
                    .withInvoice(requiredInvoiceId);
            invoiceLineRepository.save(Objects.requireNonNull(toSave, "invoice line must not be null"));
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
