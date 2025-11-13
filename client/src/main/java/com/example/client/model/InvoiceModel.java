package com.example.client.model;

import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.enums.InvoiceStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modello JavaFX che incapsula InvoiceDTO per il binding con la vista.
 */
public class InvoiceModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty number = new SimpleStringProperty();
    private final ObjectProperty<Long> contractId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> customerId = new SimpleObjectProperty<>();
    private final StringProperty customerName = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> issueDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();
    private final StringProperty notes = new SimpleStringProperty();
    private List<InvoiceLineDTO> lines = new ArrayList<>();

    public static InvoiceModel fromDto(InvoiceDTO dto) {
        InvoiceModel model = new InvoiceModel();
        model.setId(dto.getId());
        model.setNumber(dto.getNumber());
        model.setContractId(dto.getContractId());
        model.setCustomerId(dto.getCustomerId());
        model.setCustomerName(dto.getCustomerName());
        model.setAmount(dto.getAmount());
        model.setIssueDate(dto.getIssueDate());
        model.setDueDate(dto.getDueDate());
        model.setPaymentDate(dto.getPaymentDate());
        model.setNotes(dto.getNotes());
        model.setLines(dto.getLines());
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus().name());
        }
        return model;
    }

    public InvoiceDTO toDto() {
        InvoiceStatus invoiceStatus = getStatus() != null && !getStatus().isBlank() ? InvoiceStatus.valueOf(getStatus()) : null;
        return new InvoiceDTO(
                getId(),
                getNumber(),
                getContractId(),
                getCustomerId(),
                getCustomerName(),
                getAmount(),
                getIssueDate(),
                getDueDate(),
                invoiceStatus,
                getPaymentDate(),
                getNotes(),
                getLines()
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

    public String getNumber() {
        return number.get();
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public StringProperty numberProperty() {
        return number;
    }

    public Long getContractId() {
        return contractId.get();
    }

    public void setContractId(Long contractId) {
        this.contractId.set(contractId);
    }

    public ObjectProperty<Long> contractIdProperty() {
        return contractId;
    }

    public Long getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(Long customerId) {
        this.customerId.set(customerId);
    }

    public ObjectProperty<Long> customerIdProperty() {
        return customerId;
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

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
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

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public ObjectProperty<LocalDate> paymentDateProperty() {
        return paymentDate;
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public List<InvoiceLineDTO> getLines() {
        return new ArrayList<>(lines);
    }

    public void setLines(List<InvoiceLineDTO> lines) {
        this.lines = lines != null ? new ArrayList<>(lines) : new ArrayList<>();
    }
}
