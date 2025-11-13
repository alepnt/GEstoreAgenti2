package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Table("articles")
public class Article {

    @Id
    private Long id;

    private String code;

    private String name;

    private String description;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("vat_rate")
    private BigDecimal vatRate;

    @Column("unit_of_measure")
    private String unitOfMeasure;

    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    public Article(Long id,
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

    public static Article create(String code,
                                 String name,
                                 String description,
                                 BigDecimal unitPrice,
                                 BigDecimal vatRate,
                                 String unitOfMeasure) {
        return new Article(null, code, name, description, unitPrice, vatRate, unitOfMeasure, null, null);
    }

    public Article withId(Long id) {
        return new Article(id, code, name, description, unitPrice, vatRate, unitOfMeasure, createdAt, updatedAt);
    }

    public Article updateFrom(Article source) {
        return new Article(id,
                source.code,
                source.name,
                source.description,
                source.unitPrice,
                source.vatRate,
                source.unitOfMeasure,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
