package com.example.server.service.mapper;

import com.example.common.dto.CommissionDTO;
import com.example.server.domain.Commission;

public final class CommissionMapper {

    private CommissionMapper() {
    }

    public static CommissionDTO toDto(Commission commission) {
        if (commission == null) {
            return null;
        }
        return new CommissionDTO(
                commission.getId(),
                commission.getAgentId(),
                commission.getContractId(),
                commission.getTotalCommission(),
                commission.getPaidCommission(),
                commission.getPendingCommission(),
                commission.getLastUpdated()
        );
    }
}
