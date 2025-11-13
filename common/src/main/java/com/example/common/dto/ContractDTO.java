package com.example.common.dto;

import com.example.common.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO condiviso per rappresentare i contratti commerciali.
 */
public class ContractDTO {

    private Long id;
    private Long agentId;
    private String customerName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalValue;
    private ContractStatus status;

    public ContractDTO() {
    }

    public ContractDTO(Long id,
                       Long agentId,
                       String customerName,
                       String description,
                       LocalDate startDate,
                       LocalDate endDate,
                       BigDecimal totalValue,
                       ContractStatus status) {
        this.id = id;
        this.agentId = agentId;
        this.customerName = customerName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalValue = totalValue;
        this.status = status;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractDTO that = (ContractDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ContractDTO{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", customerName='" + customerName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalValue=" + totalValue +
                ", status=" + status +
                '}';
    }
}
