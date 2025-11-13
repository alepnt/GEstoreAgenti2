package com.example.common.dto;

import com.example.common.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO condiviso per rappresentare le fatture.
 */
public class InvoiceDTO {

    private Long id;
    private String number;
    private Long contractId;
    private String customerName;
    private BigDecimal amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private LocalDate paymentDate;
    private String notes;

    public InvoiceDTO() {
    }

    public InvoiceDTO(Long id,
                      String number,
                      Long contractId,
                      String customerName,
                      BigDecimal amount,
                      LocalDate issueDate,
                      LocalDate dueDate,
                      InvoiceStatus status,
                      LocalDate paymentDate,
                      String notes) {
        this.id = id;
        this.number = number;
        this.contractId = contractId;
        this.customerName = customerName;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.paymentDate = paymentDate;
        this.notes = notes;
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
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
