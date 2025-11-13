package com.example.server.repository;

import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentHistoryRepository extends CrudRepository<DocumentHistory, Long> {

    List<DocumentHistory> findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(DocumentType documentType, Long documentId);
}
