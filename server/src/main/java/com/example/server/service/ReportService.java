package com.example.server.service;

import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Contract;
import com.example.server.domain.Invoice;
import com.example.server.repository.ContractRepository;
import com.example.server.repository.InvoiceRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportService {

    private static final float MARGIN = 40f;
    private static final float ROW_HEIGHT = 16f;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final CommissionService commissionService;

    public ReportService(InvoiceRepository invoiceRepository,
                         ContractRepository contractRepository,
                         CommissionService commissionService) {
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
        this.commissionService = commissionService;
    }

    public byte[] generateClosedInvoicesReport(LocalDate from, LocalDate to, Long agentId) {
        List<InvoiceReportRow> rows = collectRows(from, to, agentId);
        BigDecimal totalInvoices = rows.stream()
                .map(InvoiceReportRow::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCommissions = rows.stream()
                .map(InvoiceReportRow::commission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = null;
            try {
                contentStream = new PDPageContentStream(document, page);
                float width = page.getMediaBox().getWidth();
                float y = page.getMediaBox().getHeight() - MARGIN;

                drawTitle(contentStream, width, y);
                y -= 28;

                y = drawSummary(contentStream, y, from, to, agentId, rows.size(), totalInvoices, totalCommissions);
                y -= 18;

                y = drawTableHeader(contentStream, y);

                for (InvoiceReportRow row : rows) {
                    if (y <= MARGIN + ROW_HEIGHT) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                        y = drawTableHeader(contentStream, y);
                    }
                    y = drawRow(contentStream, y, row);
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Impossibile generare il report PDF", ex);
        }
    }

    private List<InvoiceReportRow> collectRows(LocalDate from, LocalDate to, Long agentId) {
        Map<Long, Contract> contractCache = new HashMap<>();
        List<InvoiceReportRow> rows = new ArrayList<>();
        for (Invoice invoice : invoiceRepository.findAllByOrderByIssueDateDesc()) {
            if (invoice.getStatus() != InvoiceStatus.PAID) {
                continue;
            }
            LocalDate paymentDate = invoice.getPaymentDate();
            if (from != null && (paymentDate == null || paymentDate.isBefore(from))) {
                continue;
            }
            if (to != null && (paymentDate == null || paymentDate.isAfter(to))) {
                continue;
            }
            Long contractId = invoice.getContractId();
            Long invoiceAgentId = null;
            if (contractId != null) {
                Contract contract = contractCache.computeIfAbsent(contractId, id -> contractRepository.findById(id).orElse(null));
                if (contract != null) {
                    invoiceAgentId = contract.getAgentId();
                }
            }
            if (agentId != null && !Objects.equals(agentId, invoiceAgentId)) {
                continue;
            }
            BigDecimal commission = commissionService.computeCommission(invoice.getAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amount = invoice.getAmount() != null ? invoice.getAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            rows.add(new InvoiceReportRow(
                    invoice.getNumber(),
                    invoice.getCustomerName(),
                    contractId,
                    invoiceAgentId,
                    paymentDate,
                    amount,
                    commission
            ));
        }
        rows.sort(Comparator.comparing(InvoiceReportRow::paymentDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(InvoiceReportRow::number, Comparator.nullsLast(String::compareToIgnoreCase)));
        return rows;
    }

    private void drawTitle(PDPageContentStream contentStream, float width, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Report fatture chiuse") / 1000 * 18;
        contentStream.newLineAtOffset((width - titleWidth) / 2, y);
        contentStream.showText("Report fatture chiuse");
        contentStream.endText();
    }

    private float drawSummary(PDPageContentStream contentStream,
                              float y,
                              LocalDate from,
                              LocalDate to,
                              Long agentId,
                              int rows,
                              BigDecimal totalInvoices,
                              BigDecimal totalCommissions) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("Periodo: " + periodLabel(from, to));
        contentStream.endText();

        y -= ROW_HEIGHT;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("Agente: " + (agentId != null ? agentId : "Tutti"));
        contentStream.endText();

        y -= ROW_HEIGHT;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("Fatture incluse: " + rows);
        contentStream.endText();

        y -= ROW_HEIGHT;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("Totale importi: " + formatCurrency(totalInvoices));
        contentStream.endText();

        y -= ROW_HEIGHT;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("Totale provvigioni: " + formatCurrency(totalCommissions));
        contentStream.endText();

        return y;
    }

    private float drawTableHeader(PDPageContentStream contentStream, float y) throws IOException {
        float x = MARGIN;
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
        y -= ROW_HEIGHT;
        drawText(contentStream, x, y, "Numero");
        x += 90;
        drawText(contentStream, x, y, "Cliente");
        x += 120;
        drawText(contentStream, x, y, "Contratto");
        x += 70;
        drawText(contentStream, x, y, "Agente");
        x += 60;
        drawText(contentStream, x, y, "Pagamento");
        x += 80;
        drawText(contentStream, x, y, "Importo");
        x += 70;
        drawText(contentStream, x, y, "Provvigione");
        return y - 4;
    }

    private float drawRow(PDPageContentStream contentStream, float y, InvoiceReportRow row) throws IOException {
        float x = MARGIN;
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        y -= ROW_HEIGHT;
        drawText(contentStream, x, y, safe(row.number()));
        x += 90;
        drawText(contentStream, x, y, safe(row.customer()));
        x += 120;
        drawText(contentStream, x, y, row.contractId() != null ? row.contractId().toString() : "-");
        x += 70;
        drawText(contentStream, x, y, row.agentId() != null ? row.agentId().toString() : "-");
        x += 60;
        drawText(contentStream, x, y, row.paymentDate() != null ? DATE_FORMATTER.format(row.paymentDate()) : "-");
        x += 80;
        drawText(contentStream, x, y, formatCurrency(row.amount()));
        x += 70;
        drawText(contentStream, x, y, formatCurrency(row.commission()));
        return y;
    }

    private void drawText(PDPageContentStream contentStream, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String formatCurrency(BigDecimal amount) {
        return amount != null ? amount.setScale(2, RoundingMode.HALF_UP).toPlainString() + " €" : "0.00 €";
    }

    private String periodLabel(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return "Tutto il periodo";
        }
        if (from != null && to != null) {
            return DATE_FORMATTER.format(from) + " - " + DATE_FORMATTER.format(to);
        }
        if (from != null) {
            return "Da " + DATE_FORMATTER.format(from);
        }
        return "Fino a " + DATE_FORMATTER.format(to);
    }

    private record InvoiceReportRow(String number,
                                    String customer,
                                    Long contractId,
                                    Long agentId,
                                    LocalDate paymentDate,
                                    BigDecimal amount,
                                    BigDecimal commission) {
    }
}
