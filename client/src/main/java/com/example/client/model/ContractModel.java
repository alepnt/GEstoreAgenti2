package com.example.client.model;

import com.example.common.dto.ContractDTO;
import com.example.common.enums.ContractStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modello JavaFX per i contratti.
 */
public class ContractModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> agentId = new SimpleObjectProperty<>();
    private final StringProperty customerName = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalValue = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();

    public static ContractModel fromDto(ContractDTO dto) {
        ContractModel model = new ContractModel();
        model.setId(dto.getId());
        model.setAgentId(dto.getAgentId());
        model.setCustomerName(dto.getCustomerName());
        model.setDescription(dto.getDescription());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setTotalValue(dto.getTotalValue());
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus().name());
        }
        return model;
    }

    public ContractDTO toDto() {
        ContractStatus contractStatus = getStatus() != null && !getStatus().isBlank() ? ContractStatus.valueOf(getStatus()) : null;
        return new ContractDTO(
                getId(),
                getAgentId(),
                getCustomerName(),
                getDescription(),
                getStartDate(),
                getEndDate(),
                getTotalValue(),
                contractStatus
        );
    }

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public Long getAgentId() {
        return agentId.get();
    }

    public void setAgentId(Long agentId) {
        this.agentId.set(agentId);
    }

    public ObjectProperty<Long> agentIdProperty() {
        return agentId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
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

    public BigDecimal getTotalValue() {
        return totalValue.get();
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue.set(totalValue);
    }

    public ObjectProperty<BigDecimal> totalValueProperty() {
        return totalValue;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }
}
