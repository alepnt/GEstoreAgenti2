package com.example.common.dto;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;

import java.time.Instant;
import java.util.Objects;

/**
 * DTO per trasferire le informazioni dello storico dei documenti.
 */
public class DocumentHistoryDTO {

    private Long id;
    private DocumentType documentType;
    private Long documentId;
    private DocumentAction action;
    private String description;
    private Instant createdAt;

    public DocumentHistoryDTO() {
    }

    public DocumentHistoryDTO(Long id,
                              DocumentType documentType,
                              Long documentId,
                              DocumentAction action,
                              String description,
                              Instant createdAt) {
        this.id = id;
        this.documentType = documentType;
        this.documentId = documentId;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public DocumentAction getAction() {
        return action;
    }

    public void setAction(DocumentAction action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentHistoryDTO that = (DocumentHistoryDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
