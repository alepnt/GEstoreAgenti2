package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Table("invoice_lines")
public class InvoiceLine {

    @Id
    private Long id;

    @Column("invoice_id")
    private Long invoiceId;

    @Column("article_id")
    private Long articleId;

    @Column("article_code")
    private String articleCode;

    private String description;

    private BigDecimal quantity;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("vat_rate")
    private BigDecimal vatRate;

    private BigDecimal total;

    public InvoiceLine(Long id,
                       Long invoiceId,
                       Long articleId,
                       String articleCode,
                       String description,
                       BigDecimal quantity,
                       BigDecimal unitPrice,
                       BigDecimal vatRate,
                       BigDecimal total) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.articleId = articleId;
        this.articleCode = articleCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
        this.total = total;
    }

    public static InvoiceLine create(Long invoiceId,
                                     Long articleId,
                                     String articleCode,
                                     String description,
                                     BigDecimal quantity,
                                     BigDecimal unitPrice,
                                     BigDecimal vatRate,
                                     BigDecimal total) {
        return new InvoiceLine(null, invoiceId, articleId, articleCode, description, quantity, unitPrice, vatRate, total);
    }

    public InvoiceLine withInvoice(Long invoiceId) {
        return new InvoiceLine(id, invoiceId, articleId, articleCode, description, quantity, unitPrice, vatRate, total);
    }

    public Long getId() {
        return id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public String getArticleCode() {
        return articleCode;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceLine that = (InvoiceLine) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
