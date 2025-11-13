package com.example.client.model;

import com.example.common.dto.InvoiceDTO;
import com.example.common.enums.InvoiceStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modello JavaFX che incapsula InvoiceDTO per il binding con la vista.
 */
public class InvoiceModel {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty customerId = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> issueDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();

    public static InvoiceModel fromDto(InvoiceDTO dto) {
        InvoiceModel model = new InvoiceModel();
        model.setId(dto.getId());
        model.setCustomerId(dto.getCustomerId());
        model.setAmount(dto.getAmount());
        model.setIssueDate(dto.getIssueDate());
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus().name());
        }
        return model;
    }

    public InvoiceDTO toDto() {
        InvoiceStatus invoiceStatus = getStatus() != null ? InvoiceStatus.valueOf(getStatus()) : null;
        return new InvoiceDTO(getId(), getCustomerId(), getAmount(), getIssueDate(), invoiceStatus);
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

    public String getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(String customerId) {
        this.customerId.set(customerId);
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public LocalDate getIssueDate() {
        return issueDate.get();
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate.set(issueDate);
    }

    public ObjectProperty<LocalDate> issueDateProperty() {
        return issueDate;
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
