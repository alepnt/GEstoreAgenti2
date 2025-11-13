package com.example.server.service.mapper;

import com.example.common.dto.CustomerDTO;
import com.example.server.domain.Customer;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getVatNumber(),
                customer.getTaxCode(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    public static Customer fromDto(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Customer(
                dto.getId(),
                dto.getName(),
                dto.getVatNumber(),
                dto.getTaxCode(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
}
