package com.example.server.service;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import com.example.server.repository.DocumentHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class DocumentHistoryService {

    private final DocumentHistoryRepository repository;
    private final Clock clock;

    public DocumentHistoryService(DocumentHistoryRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public DocumentHistory log(DocumentType type, Long documentId, DocumentAction action, String description) {
        DocumentHistory history = DocumentHistory.create(type, documentId, action, description, Instant.now(clock));
        return repository.save(history);
    }

    public List<DocumentHistory> list(DocumentType type, Long documentId) {
        return repository.findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(type, documentId);
    }
}
