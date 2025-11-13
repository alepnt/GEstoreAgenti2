package com.example.common.api;

import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione dei contratti.
 */
public interface ContractApiContract {

    List<ContractDTO> listContracts();

    Optional<ContractDTO> findById(Long id);

    ContractDTO create(ContractDTO contractDTO);

    ContractDTO update(Long id, ContractDTO contractDTO);

    void delete(Long id);

    List<DocumentHistoryDTO> history(Long id);
}
