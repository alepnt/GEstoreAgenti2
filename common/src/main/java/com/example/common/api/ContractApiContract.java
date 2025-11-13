package com.example.common.api;

import com.example.common.dto.ContractDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione dei contratti.
 */
public interface ContractApiContract {

    List<ContractDTO> listContracts();

    Optional<ContractDTO> findById(String id);

    ContractDTO create(ContractDTO contractDTO);

    ContractDTO update(String id, ContractDTO contractDTO);

    void delete(String id);
}
