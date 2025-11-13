package com.example.client.model;

import com.example.common.dto.ArticleDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

/**
 * Modello JavaFX per la gestione del catalogo articoli.
 */
public class ArticleModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty code = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> vatRate = new SimpleObjectProperty<>();
    private final StringProperty unitOfMeasure = new SimpleStringProperty();

    public static ArticleModel fromDto(ArticleDTO dto) {
        ArticleModel model = new ArticleModel();
        model.setId(dto.getId());
        model.setCode(dto.getCode());
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setUnitPrice(dto.getUnitPrice());
        model.setVatRate(dto.getVatRate());
        model.setUnitOfMeasure(dto.getUnitOfMeasure());
        return model;
    }

    public ArticleDTO toDto() {
        return new ArticleDTO(
                getId(),
                getCode(),
                getName(),
                getDescription(),
                getUnitPrice(),
                getVatRate(),
                getUnitOfMeasure(),
                null,
                null
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

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public StringProperty codeProperty() {
        return code;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
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

    public BigDecimal getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return unitPrice;
    }

    public BigDecimal getVatRate() {
        return vatRate.get();
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate.set(vatRate);
    }

    public ObjectProperty<BigDecimal> vatRateProperty() {
        return vatRate;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure.get();
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure.set(unitOfMeasure);
    }

    public StringProperty unitOfMeasureProperty() {
        return unitOfMeasure;
    }

    @Override
    public String toString() {
        if (getCode() != null && !getCode().isBlank()) {
            return getCode() + " - " + (getName() != null ? getName() : "");
        }
        return getName() != null ? getName() : "";
    }
}
