package com.example.client.model;

import com.example.common.dto.InvoiceLineDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Modello di riga fattura utilizzato nel client.
 */
public class InvoiceLineModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> articleId = new SimpleObjectProperty<>();
    private final StringProperty articleCode = new SimpleStringProperty();
    private final StringProperty articleName = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> quantity = new SimpleObjectProperty<>(BigDecimal.ONE);
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> vatRate = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ReadOnlyObjectWrapper<BigDecimal> total = new ReadOnlyObjectWrapper<>(BigDecimal.ZERO);

    public InvoiceLineModel() {
        quantity.addListener((obs, oldValue, newValue) -> recalculateTotal());
        unitPrice.addListener((obs, oldValue, newValue) -> recalculateTotal());
        vatRate.addListener((obs, oldValue, newValue) -> recalculateTotal());
    }

    public static InvoiceLineModel fromDto(InvoiceLineDTO dto) {
        InvoiceLineModel model = new InvoiceLineModel();
        model.setId(dto.getId());
        model.setArticleId(dto.getArticleId());
        model.setArticleCode(dto.getArticleCode());
        model.setDescription(dto.getDescription());
        model.setQuantity(dto.getQuantity());
        model.setUnitPrice(dto.getUnitPrice());
        model.setVatRate(dto.getVatRate());
        model.setTotal(dto.getTotal());
        return model;
    }

    public InvoiceLineDTO toDto() {
        return new InvoiceLineDTO(
                getId(),
                null,
                getArticleId(),
                getArticleCode(),
                getDescription(),
                getQuantity(),
                getUnitPrice(),
                getVatRate(),
                getTotal()
        );
    }

    public void recalculateTotal() {
        BigDecimal qty = getQuantity() != null ? getQuantity() : BigDecimal.ZERO;
        BigDecimal price = getUnitPrice() != null ? getUnitPrice() : BigDecimal.ZERO;
        BigDecimal rate = getVatRate() != null ? getVatRate() : BigDecimal.ZERO;
        BigDecimal subtotal = price.multiply(qty).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vatAmount = rate.compareTo(BigDecimal.ZERO) > 0 ? subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        total.set(subtotal.add(vatAmount));
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

    public Long getArticleId() {
        return articleId.get();
    }

    public void setArticleId(Long articleId) {
        this.articleId.set(articleId);
    }

    public ObjectProperty<Long> articleIdProperty() {
        return articleId;
    }

    public String getArticleCode() {
        return articleCode.get();
    }

    public void setArticleCode(String articleCode) {
        this.articleCode.set(articleCode);
    }

    public StringProperty articleCodeProperty() {
        return articleCode;
    }

    public String getArticleName() {
        return articleName.get();
    }

    public void setArticleName(String articleName) {
        this.articleName.set(articleName);
    }

    public StringProperty articleNameProperty() {
        return articleName;
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

    public BigDecimal getQuantity() {
        return quantity.get();
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity.set(quantity);
    }

    public ObjectProperty<BigDecimal> quantityProperty() {
        return quantity;
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

    public BigDecimal getTotal() {
        return total.get();
    }

    public void setTotal(BigDecimal value) {
        total.set(value != null ? value : BigDecimal.ZERO);
    }

    public ReadOnlyObjectProperty<BigDecimal> totalProperty() {
        return total.getReadOnlyProperty();
    }
}
