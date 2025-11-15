package com.example.server.service;

import com.example.common.dto.CustomerDTO;
import com.example.server.domain.Customer;
import com.example.server.repository.CustomerRepository;
import com.example.server.service.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAllByOrderByNameAsc().stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(CustomerMapper::toDto);
    }

    @Transactional
    public CustomerDTO create(CustomerDTO dto) {
        CustomerDTO validatedDto = Objects.requireNonNull(dto, "customer must not be null");
        validate(validatedDto);
        Customer customer = Objects.requireNonNull(CustomerMapper.fromDto(validatedDto),
                "mapped customer must not be null");
        Customer toSave = Objects.requireNonNull(Customer.create(
                normalize(customer.getName()),
                normalize(customer.getVatNumber()),
                normalize(customer.getTaxCode()),
                normalize(customer.getEmail()),
                normalize(customer.getPhone()),
                normalize(customer.getAddress())
        ), "created customer must not be null");
        Customer saved = customerRepository.save(toSave);
        return CustomerMapper.toDto(saved);
    }

    @Transactional
    public Optional<CustomerDTO> update(Long id, CustomerDTO dto) {
        CustomerDTO validatedDto = Objects.requireNonNull(dto, "customer must not be null");
        validate(validatedDto);
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    Customer updateSource = Objects.requireNonNull(Customer.create(
                            normalize(validatedDto.getName()),
                            normalize(validatedDto.getVatNumber()),
                            normalize(validatedDto.getTaxCode()),
                            normalize(validatedDto.getEmail()),
                            normalize(validatedDto.getPhone()),
                            normalize(validatedDto.getAddress())
                    ), "created customer must not be null");
                    Customer updated = Objects.requireNonNull(existing.updateFrom(updateSource),
                            "updated customer must not be null");
                    Customer saved = customerRepository.save(updated);
                    return CustomerMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    customerRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Customer require(Long id) {
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));
    }

    private void validate(CustomerDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome del cliente è obbligatorio");
        }
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
