package com.example.server.repository;

import com.example.server.domain.Contract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends CrudRepository<Contract, Long> {

    List<Contract> findAllByOrderByStartDateDesc();
}
