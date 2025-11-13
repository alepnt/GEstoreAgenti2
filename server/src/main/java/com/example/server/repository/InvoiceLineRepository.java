package com.example.server.repository;

import com.example.server.domain.InvoiceLine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceLineRepository extends CrudRepository<InvoiceLine, Long> {

    List<InvoiceLine> findByInvoiceIdOrderById(Long invoiceId);

    void deleteByInvoiceId(Long invoiceId);
}
