package com.example.server.domain;

import com.example.common.enums.InvoiceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Table("invoices")
public class Invoice {

    @Id
    private Long id;

    @Column("contract_id")
    private Long contractId;

    @Column("invoice_number")
    private String number;

    @Column("customer_name")
    private String customerName;

    private BigDecimal amount;

    @Column("issue_date")
    private LocalDate issueDate;

    @Column("due_date")
    private LocalDate dueDate;

    private InvoiceStatus status;

    @Column("payment_date")
    private LocalDate paymentDate;

    private String notes;

    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    public Invoice(Long id,
                   Long contractId,
                   String number,
                   String customerName,
                   BigDecimal amount,
                   LocalDate issueDate,
                   LocalDate dueDate,
                   InvoiceStatus status,
                   LocalDate paymentDate,
                   String notes,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.number = number;
        this.customerName = customerName;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.paymentDate = paymentDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Invoice create(Long contractId,
                                 String number,
                                 String customerName,
                                 BigDecimal amount,
                                 LocalDate issueDate,
                                 LocalDate dueDate,
                                 InvoiceStatus status,
                                 String notes) {
        return new Invoice(null, contractId, number, customerName, amount, issueDate, dueDate, status, null, notes, null, null);
    }

    public Invoice withId(Long id) {
        return new Invoice(id, contractId, number, customerName, amount, issueDate, dueDate, status, paymentDate, notes, createdAt, updatedAt);
    }

    public Invoice updateFrom(Invoice source) {
        return new Invoice(id,
                source.contractId,
                source.number,
                source.customerName,
                source.amount,
                source.issueDate,
                source.dueDate,
                source.status,
                source.paymentDate,
                source.notes,
                createdAt,
                updatedAt);
    }

    public Invoice registerPayment(LocalDate paymentDate, InvoiceStatus newStatus) {
        return new Invoice(id, contractId, number, customerName, amount, issueDate, dueDate, newStatus, paymentDate, notes, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getNumber() {
        return number;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice invoice)) return false;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
