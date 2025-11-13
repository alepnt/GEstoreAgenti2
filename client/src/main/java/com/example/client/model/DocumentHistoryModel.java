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

    private final StringProperty action = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<Instant> createdAt = new SimpleObjectProperty<>();

    public static DocumentHistoryModel fromDto(DocumentHistoryDTO dto) {
        DocumentHistoryModel model = new DocumentHistoryModel();
        model.setAction(dto.getAction().name());
        model.setDescription(dto.getDescription());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
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
