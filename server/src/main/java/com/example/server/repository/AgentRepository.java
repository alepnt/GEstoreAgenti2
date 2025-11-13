package com.example.server.repository;

import com.example.server.domain.Agent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends CrudRepository<Agent, Long> {

    Optional<Agent> findByUserId(Long userId);
}
