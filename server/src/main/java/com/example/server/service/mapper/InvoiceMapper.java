package com.example.server.service.mapper;

import com.example.common.dto.InvoiceDTO;
import com.example.server.domain.Invoice;

public final class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceDTO toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return new InvoiceDTO(
                invoice.getId(),
                invoice.getNumber(),
                invoice.getContractId(),
                invoice.getCustomerName(),
                invoice.getAmount(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getStatus(),
                invoice.getPaymentDate(),
                invoice.getNotes()
        );
    }

    public static Invoice fromDto(InvoiceDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Invoice(
                dto.getId(),
                dto.getContractId(),
                dto.getNumber(),
                dto.getCustomerName(),
                dto.getAmount(),
                dto.getIssueDate(),
                dto.getDueDate(),
                dto.getStatus(),
                dto.getPaymentDate(),
                dto.getNotes(),
                null,
                null
        );
    }
}
