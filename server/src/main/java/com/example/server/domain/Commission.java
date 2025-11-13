package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Table("commissions")
public class Commission {

    @Id
    private Long id;

    @Column("agent_id")
    private Long agentId;

    @Column("contract_id")
    private Long contractId;

    @Column("total_commission")
    private BigDecimal totalCommission;

    @Column("paid_commission")
    private BigDecimal paidCommission;

    @Column("pending_commission")
    private BigDecimal pendingCommission;

    @Column("last_updated")
    private Instant lastUpdated;

    public Commission(Long id,
                      Long agentId,
                      Long contractId,
                      BigDecimal totalCommission,
                      BigDecimal paidCommission,
                      BigDecimal pendingCommission,
                      Instant lastUpdated) {
        this.id = id;
        this.agentId = agentId;
        this.contractId = contractId;
        this.totalCommission = totalCommission;
        this.paidCommission = paidCommission;
        this.pendingCommission = pendingCommission;
        this.lastUpdated = lastUpdated;
    }

    public static Commission create(Long agentId, Long contractId, BigDecimal totalCommission) {
        return new Commission(null, agentId, contractId, totalCommission, BigDecimal.ZERO, totalCommission, Instant.now());
    }

    public Commission update(BigDecimal totalCommission, BigDecimal paidCommission, BigDecimal pendingCommission, Instant lastUpdated) {
        return new Commission(id, agentId, contractId, totalCommission, paidCommission, pendingCommission, lastUpdated);
    }

    public Long getId() {
        return id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public Long getContractId() {
        return contractId;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public BigDecimal getPaidCommission() {
        return paidCommission;
    }

    public BigDecimal getPendingCommission() {
        return pendingCommission;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commission that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
