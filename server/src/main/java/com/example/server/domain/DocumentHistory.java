package com.example.server.domain;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table("document_history")
public class DocumentHistory {

    @Id
    private Long id;

    @Column("document_type")
    private DocumentType documentType;

    @Column("document_id")
    private Long documentId;

    private DocumentAction action;

    private String description;

    @Column("created_at")
    private Instant createdAt;

    public DocumentHistory(Long id,
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

    public static DocumentHistory create(DocumentType type, Long documentId, DocumentAction action, String description, Instant createdAt) {
        return new DocumentHistory(null, type, documentId, action, description, createdAt);
    }

    public Long getId() {
        return id;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public DocumentAction getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentHistory that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
