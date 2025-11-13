package com.example.client.model;

import com.example.common.dto.CustomerDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello JavaFX per il binding dell'anagrafica clienti.
 */
public class CustomerModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty vatNumber = new SimpleStringProperty();
    private final StringProperty taxCode = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();

    public static CustomerModel fromDto(CustomerDTO dto) {
        CustomerModel model = new CustomerModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setVatNumber(dto.getVatNumber());
        model.setTaxCode(dto.getTaxCode());
        model.setEmail(dto.getEmail());
        model.setPhone(dto.getPhone());
        model.setAddress(dto.getAddress());
        return model;
    }

    public CustomerDTO toDto() {
        return new CustomerDTO(
                getId(),
                getName(),
                getVatNumber(),
                getTaxCode(),
                getEmail(),
                getPhone(),
                getAddress(),
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getVatNumber() {
        return vatNumber.get();
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber.set(vatNumber);
    }

    public StringProperty vatNumberProperty() {
        return vatNumber;
    }

    public String getTaxCode() {
        return taxCode.get();
    }

    public void setTaxCode(String taxCode) {
        this.taxCode.set(taxCode);
    }

    public StringProperty taxCodeProperty() {
        return taxCode;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    @Override
    public String toString() {
        return getName() != null ? getName() : "";
    }
}
