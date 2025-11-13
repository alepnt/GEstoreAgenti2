package com.example.server.service;

import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.Contract;
import com.example.server.repository.ContractRepository;
import com.example.server.service.mapper.ContractMapper;
import com.example.server.service.mapper.DocumentHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final DocumentHistoryService documentHistoryService;

    public ContractService(ContractRepository contractRepository, DocumentHistoryService documentHistoryService) {
        this.contractRepository = contractRepository;
        this.documentHistoryService = documentHistoryService;
    }

    public List<ContractDTO> findAll() {
        return contractRepository.findAllByOrderByStartDateDesc().stream()
                .map(ContractMapper::toDto)
                .toList();
    }

    public Optional<ContractDTO> findById(Long id) {
        return contractRepository.findById(id).map(ContractMapper::toDto);
    }

    public ContractDTO create(ContractDTO dto) {
        Contract contract = ContractMapper.fromDto(dto);
        Contract saved = contractRepository.save(contract);
        documentHistoryService.log(DocumentType.CONTRACT, saved.getId(), DocumentAction.CREATED, "Contratto creato: " + saved.getDescription());
        return ContractMapper.toDto(saved);
    }

    public Optional<ContractDTO> update(Long id, ContractDTO dto) {
        return contractRepository.findById(id)
                .map(existing -> existing.updateFrom(ContractMapper.fromDto(dto)))
                .map(contractRepository::save)
                .map(saved -> {
                    documentHistoryService.log(DocumentType.CONTRACT, saved.getId(), DocumentAction.UPDATED, "Contratto aggiornato");
                    return ContractMapper.toDto(saved);
                });
    }

    public boolean delete(Long id) {
        return contractRepository.findById(id)
                .map(contract -> {
                    contractRepository.deleteById(id);
                    documentHistoryService.log(DocumentType.CONTRACT, contract.getId(), DocumentAction.DELETED, "Contratto eliminato");
                    return true;
                })
                .orElse(false);
    }

    public List<DocumentHistoryDTO> history(Long id) {
        return documentHistoryService.list(DocumentType.CONTRACT, id).stream()
                .map(DocumentHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
