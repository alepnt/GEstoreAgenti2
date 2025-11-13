package com.example.server.repository;

import com.example.server.domain.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

    List<Invoice> findAllByOrderByIssueDateDesc();
}
