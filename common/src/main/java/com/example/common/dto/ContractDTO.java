package com.example.common.dto;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO condiviso per rappresentare i contratti commerciali.
 */
public class ContractDTO {

    private String id;
    private String agentId;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public ContractDTO() {
    }

    public ContractDTO(String id, String agentId, String description, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.agentId = agentId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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
                "id='" + id + '\'' +
                ", agentId='" + agentId + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
