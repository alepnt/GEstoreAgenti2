package com.example.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO per rappresentare un articolo vendibile inseribile nelle fatture.
 */
public class ArticleDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal vatRate;
    private String unitOfMeasure;
    private Instant createdAt;
    private Instant updatedAt;

    public ArticleDTO() {
    }

    public ArticleDTO(Long id,
                      String code,
                      String name,
                      String description,
                      BigDecimal unitPrice,
                      BigDecimal vatRate,
                      String unitOfMeasure,
                      Instant createdAt,
                      Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
        this.unitOfMeasure = unitOfMeasure;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleDTO that = (ArticleDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArticleDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
