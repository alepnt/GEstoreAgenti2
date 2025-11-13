package com.example.server.service;

import com.example.common.dto.CustomerDTO;
import com.example.server.domain.Customer;
import com.example.server.repository.CustomerRepository;
import com.example.server.service.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
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
        return customerRepository.findById(id).map(CustomerMapper::toDto);
    }

    @Transactional
    public CustomerDTO create(CustomerDTO dto) {
        validate(dto);
        Customer customer = CustomerMapper.fromDto(dto);
        Customer saved = customerRepository.save(Customer.create(
                normalize(customer.getName()),
                normalize(customer.getVatNumber()),
                normalize(customer.getTaxCode()),
                normalize(customer.getEmail()),
                normalize(customer.getPhone()),
                normalize(customer.getAddress())
        ));
        return CustomerMapper.toDto(saved);
    }

    @Transactional
    public Optional<CustomerDTO> update(Long id, CustomerDTO dto) {
        validate(dto);
        return customerRepository.findById(id)
                .map(existing -> {
                    Customer updated = existing.updateFrom(Customer.create(
                            normalize(dto.getName()),
                            normalize(dto.getVatNumber()),
                            normalize(dto.getTaxCode()),
                            normalize(dto.getEmail()),
                            normalize(dto.getPhone()),
                            normalize(dto.getAddress())
                    ));
                    Customer saved = customerRepository.save(updated);
                    return CustomerMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return customerRepository.findById(id)
                .map(existing -> {
                    customerRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Customer require(Long id) {
        return customerRepository.findById(id)
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
