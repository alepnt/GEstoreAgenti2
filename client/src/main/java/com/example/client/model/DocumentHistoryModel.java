package com.example.client.model;

import com.example.common.dto.DocumentHistoryDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;

/**
 * Modello JavaFX per rappresentare una voce di storico documentale.
 */
public class DocumentHistoryModel {

    private final StringProperty documentType = new SimpleStringProperty();
    private final StringProperty documentId = new SimpleStringProperty();
    private final StringProperty action = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<Instant> createdAt = new SimpleObjectProperty<>();

    public static DocumentHistoryModel fromDto(DocumentHistoryDTO dto) {
        DocumentHistoryModel model = new DocumentHistoryModel();
        model.setDocumentType(dto.getDocumentType() != null ? dto.getDocumentType().name() : "");
        model.setDocumentId(dto.getDocumentId() != null ? dto.getDocumentId().toString() : "");
        model.setAction(dto.getAction().name());
        model.setDescription(dto.getDescription());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }

    public String getDocumentType() {
        return documentType.get();
    }

    public void setDocumentType(String value) {
        documentType.set(value);
    }

    public StringProperty documentTypeProperty() {
        return documentType;
    }

    public String getDocumentId() {
        return documentId.get();
    }

    public void setDocumentId(String value) {
        documentId.set(value);
    }

    public StringProperty documentIdProperty() {
        return documentId;
    }

    public String getAction() {
        return action.get();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public StringProperty actionProperty() {
        return action;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<Instant> createdAtProperty() {
        return createdAt;
    }
}
