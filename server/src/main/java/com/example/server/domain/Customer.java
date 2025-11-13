package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table("customers")
public class Customer {

    @Id
    private Long id;

    private String name;

    @Column("vat_number")
    private String vatNumber;

    @Column("tax_code")
    private String taxCode;

    private String email;

    private String phone;

    private String address;

    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    public Customer(Long id,
                    String name,
                    String vatNumber,
                    String taxCode,
                    String email,
                    String phone,
                    String address,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.taxCode = taxCode;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Customer create(String name,
                                  String vatNumber,
                                  String taxCode,
                                  String email,
                                  String phone,
                                  String address) {
        return new Customer(null, name, vatNumber, taxCode, email, phone, address, null, null);
    }

    public Customer withId(Long id) {
        return new Customer(id, name, vatNumber, taxCode, email, phone, address, createdAt, updatedAt);
    }

    public Customer updateFrom(Customer source) {
        return new Customer(id,
                source.name,
                source.vatNumber,
                source.taxCode,
                source.email,
                source.phone,
                source.address,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
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
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
