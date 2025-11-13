package com.example.common.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO per rappresentare una riga di fattura con riferimento all'articolo.
 */
public class InvoiceLineDTO {

    private Long id;
    private Long invoiceId;
    private Long articleId;
    private String articleCode;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal vatRate;
    private BigDecimal total;

    public InvoiceLineDTO() {
    }

    public InvoiceLineDTO(Long id,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getArticleCode() {
        return articleCode;
    }

    public void setArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceLineDTO that = (InvoiceLineDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "InvoiceLineDTO{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", articleId=" + articleId +
                ", articleCode='" + articleCode + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", total=" + total +
                '}';
    }
}
