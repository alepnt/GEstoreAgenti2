package com.example.common.api;

import com.example.common.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione dell'anagrafica clienti.
 */
public interface CustomerApiContract {

    List<CustomerDTO> listCustomers();

    Optional<CustomerDTO> findById(Long id);

    CustomerDTO create(CustomerDTO customer);

    CustomerDTO update(Long id, CustomerDTO customer);

    void delete(Long id);
}
