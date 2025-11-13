package com.example.server.repository;

import com.example.server.domain.Commission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends CrudRepository<Commission, Long> {

    Optional<Commission> findByAgentIdAndContractId(Long agentId, Long contractId);

    List<Commission> findByAgentId(Long agentId);
}
