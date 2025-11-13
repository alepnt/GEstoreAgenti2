package com.example.client.model;

import com.example.common.dto.ContractDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Modello JavaFX per i contratti.
 */
public class ContractModel {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty agentId = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    public static ContractModel fromDto(ContractDTO dto) {
        ContractModel model = new ContractModel();
        model.setId(dto.getId());
        model.setAgentId(dto.getAgentId());
        model.setDescription(dto.getDescription());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        return model;
    }

    public ContractDTO toDto() {
        return new ContractDTO(getId(), getAgentId(), getDescription(), getStartDate(), getEndDate());
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getAgentId() {
        return agentId.get();
    }

    public void setAgentId(String agentId) {
        this.agentId.set(agentId);
    }

    public StringProperty agentIdProperty() {
        return agentId;
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

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }
}
