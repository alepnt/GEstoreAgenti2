package com.example.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO con i dati aggregati delle commissioni.
 */
public class CommissionDTO {

    private Long id;
    private Long agentId;
    private Long contractId;
    private BigDecimal totalCommission;
    private BigDecimal paidCommission;
    private BigDecimal pendingCommission;
    private Instant lastUpdated;

    public CommissionDTO() {
    }

    public CommissionDTO(Long id,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public BigDecimal getPaidCommission() {
        return paidCommission;
    }

    public void setPaidCommission(BigDecimal paidCommission) {
        this.paidCommission = paidCommission;
    }

    public BigDecimal getPendingCommission() {
        return pendingCommission;
    }

    public void setPendingCommission(BigDecimal pendingCommission) {
        this.pendingCommission = pendingCommission;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommissionDTO that = (CommissionDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
