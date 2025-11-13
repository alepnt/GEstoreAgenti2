package com.example.server.repository;

import com.example.server.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findAllByOrderByNameAsc();

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByVatNumberIgnoreCase(String vatNumber);
}
