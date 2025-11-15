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
import java.util.Objects;
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
        return contractRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(ContractMapper::toDto);
    }

    public ContractDTO create(ContractDTO dto) {
        ContractDTO requiredDto = Objects.requireNonNull(dto, "contract must not be null");
        Contract contract = Objects.requireNonNull(ContractMapper.fromDto(requiredDto),
                "mapped contract must not be null");
        Contract saved = contractRepository.save(contract);
        documentHistoryService.log(DocumentType.CONTRACT,
                Objects.requireNonNull(saved.getId(), "contract id must not be null"),
                DocumentAction.CREATED,
                "Contratto creato: " + saved.getDescription());
        return ContractMapper.toDto(saved);
    }

    public Optional<ContractDTO> update(Long id, ContractDTO dto) {
        ContractDTO requiredDto = Objects.requireNonNull(dto, "contract must not be null");
        return contractRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> Objects.requireNonNull(existing.updateFrom(Objects.requireNonNull(
                        ContractMapper.fromDto(requiredDto), "mapped contract must not be null")),
                        "updated contract must not be null"))
                .map(contractRepository::save)
                .map(saved -> {
                    documentHistoryService.log(DocumentType.CONTRACT,
                            Objects.requireNonNull(saved.getId(), "contract id must not be null"),
                            DocumentAction.UPDATED,
                            "Contratto aggiornato");
                    return ContractMapper.toDto(saved);
                });
    }

    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return contractRepository.findById(requiredId)
                .map(contract -> {
                    contractRepository.deleteById(requiredId);
                    documentHistoryService.log(DocumentType.CONTRACT,
                            Objects.requireNonNull(contract.getId(), "contract id must not be null"),
                            DocumentAction.DELETED,
                            "Contratto eliminato");
                    return true;
                })
                .orElse(false);
    }

    public List<DocumentHistoryDTO> history(Long id) {
        return documentHistoryService.list(DocumentType.CONTRACT, Objects.requireNonNull(id, "id must not be null")).stream()
                .map(DocumentHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
