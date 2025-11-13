package com.example.server.service.mapper;

import com.example.common.dto.ContractDTO;
import com.example.server.domain.Contract;

public final class ContractMapper {

    private ContractMapper() {
    }

    public static ContractDTO toDto(Contract contract) {
        if (contract == null) {
            return null;
        }
        return new ContractDTO(
                contract.getId(),
                contract.getAgentId(),
                contract.getCustomerName(),
                contract.getDescription(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getTotalValue(),
                contract.getStatus()
        );
    }

    public static Contract fromDto(ContractDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Contract(
                dto.getId(),
                dto.getAgentId(),
                dto.getCustomerName(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getTotalValue(),
                dto.getStatus()
        );
    }
}
