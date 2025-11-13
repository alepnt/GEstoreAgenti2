package com.example.server.domain;

import com.example.common.enums.ContractStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Table("contracts")
public class Contract {

    @Id
    private Long id;

    @Column("agent_id")
    private Long agentId;

    @Column("customer_name")
    private String customerName;

    private String description;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("total_value")
    private BigDecimal totalValue;

    private ContractStatus status;

    public Contract(Long id,
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

    public static Contract create(Long agentId,
                                  String customerName,
                                  String description,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  BigDecimal totalValue,
                                  ContractStatus status) {
        return new Contract(null, agentId, customerName, description, startDate, endDate, totalValue, status);
    }

    public Contract updateFrom(Contract source) {
        return new Contract(id,
                source.agentId,
                source.customerName,
                source.description,
                source.startDate,
                source.endDate,
                source.totalValue,
                source.status);
    }

    public Long getId() {
        return id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public ContractStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract contract)) return false;
        return Objects.equals(id, contract.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
