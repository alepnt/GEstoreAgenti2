package com.example.server.service.mapper;

import com.example.common.dto.InvoiceLineDTO;
import com.example.server.domain.InvoiceLine;

public final class InvoiceLineMapper {

    private InvoiceLineMapper() {
    }

    public static InvoiceLineDTO toDto(InvoiceLine line) {
        if (line == null) {
            return null;
        }
        return new InvoiceLineDTO(
                line.getId(),
                line.getInvoiceId(),
                line.getArticleId(),
                line.getArticleCode(),
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getVatRate(),
                line.getTotal()
        );
    }

    public static InvoiceLine fromDto(Long invoiceId, InvoiceLineDTO dto) {
        if (dto == null) {
            return null;
        }
        return new InvoiceLine(
                dto.getId(),
                invoiceId,
                dto.getArticleId(),
                dto.getArticleCode(),
                dto.getDescription(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getVatRate(),
                dto.getTotal()
        );
    }
}
