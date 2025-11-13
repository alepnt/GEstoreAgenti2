package com.example.common.dto;

import com.example.common.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO condiviso per rappresentare le fatture.
 */
public class InvoiceDTO {

    private Long id;
    private String number;
    private Long contractId;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private LocalDate paymentDate;
    private String notes;
    private List<InvoiceLineDTO> lines = new ArrayList<>();

    public InvoiceDTO() {
    }

    public InvoiceDTO(Long id,
                      String number,
                      Long contractId,
                      Long customerId,
                      String customerName,
                      BigDecimal amount,
                      LocalDate issueDate,
                      LocalDate dueDate,
                      InvoiceStatus status,
                      LocalDate paymentDate,
                      String notes,
                      List<InvoiceLineDTO> lines) {
        this.id = id;
        this.number = number;
        this.contractId = contractId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.paymentDate = paymentDate;
        this.notes = notes;
        if (lines != null) {
            this.lines = new ArrayList<>(lines);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<InvoiceLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLineDTO> lines) {
        this.lines = lines != null ? new ArrayList<>(lines) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceDTO that = (InvoiceDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", contractId=" + contractId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", notes='" + notes + '\'' +
                ", lines=" + lines +
                '}';
    }
}
