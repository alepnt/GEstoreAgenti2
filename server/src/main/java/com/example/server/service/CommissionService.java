package com.example.server.service;

import com.example.server.domain.Commission;
import com.example.server.domain.Contract;
import com.example.server.repository.CommissionRepository;
import com.example.server.repository.ContractRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class CommissionService {

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10");

    private final CommissionRepository commissionRepository;
    private final ContractRepository contractRepository;
    private final Clock clock;

    public CommissionService(CommissionRepository commissionRepository,
                             ContractRepository contractRepository,
                             Clock clock) {
        this.commissionRepository = commissionRepository;
        this.contractRepository = contractRepository;
        this.clock = clock;
    }

    public Optional<Commission> updateAfterPayment(Long contractId, BigDecimal invoiceAmount, BigDecimal amountPaid) {
        if (contractId == null) {
            return Optional.empty();
        }

        Optional<Contract> contract = contractRepository.findById(contractId);
        if (contract.isEmpty()) {
            return Optional.empty();
        }

        Long agentId = contract.get().getAgentId();
        BigDecimal commissionValue = invoiceAmount.multiply(COMMISSION_RATE);
        BigDecimal paidCommissionValue = amountPaid.multiply(COMMISSION_RATE);

        Commission base = commissionRepository
                .findByAgentIdAndContractId(agentId, contractId)
                .orElseGet(() -> Commission.create(agentId, contractId, BigDecimal.ZERO));

        BigDecimal totalCommission = base.getTotalCommission().add(commissionValue);
        BigDecimal paidCommission = base.getPaidCommission().add(paidCommissionValue);
        BigDecimal pendingCommission = totalCommission.subtract(paidCommission);
        if (pendingCommission.signum() < 0) {
            pendingCommission = BigDecimal.ZERO;
        }

        Commission updated = base.update(totalCommission, paidCommission, pendingCommission, Instant.now(clock));
        return Optional.of(commissionRepository.save(updated));
    }
}
