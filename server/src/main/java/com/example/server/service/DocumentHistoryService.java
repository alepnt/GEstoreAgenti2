package com.example.server.service;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import com.example.server.repository.DocumentHistoryQueryRepository;
import com.example.server.repository.DocumentHistoryRepository;
import com.example.server.service.mapper.DocumentHistoryMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class DocumentHistoryService {

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 200;
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final DocumentHistoryRepository repository;
    private final DocumentHistoryQueryRepository queryRepository;
    private final Clock clock;

    public DocumentHistoryService(DocumentHistoryRepository repository,
                                  DocumentHistoryQueryRepository queryRepository,
                                  Clock clock) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.clock = clock;
    }

    @CacheEvict(cacheNames = "documentHistorySearch", allEntries = true)
    public DocumentHistory log(DocumentType type, Long documentId, DocumentAction action, String description) {
        DocumentHistory history = Objects.requireNonNull(
                DocumentHistory.create(type, documentId, action, description, Instant.now(clock)),
                "document history must not be null");
        return repository.save(history);
    }

    public List<DocumentHistory> list(DocumentType type, Long documentId) {
        DocumentType requiredType = Objects.requireNonNull(type, "type must not be null");
        Long requiredDocumentId = Objects.requireNonNull(documentId, "documentId must not be null");
        return repository.findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(requiredType, requiredDocumentId);
    }

    @Cacheable(cacheNames = "documentHistorySearch", key = "#query.cacheKey()")
    public DocumentHistoryPageDTO search(DocumentHistoryQuery query) {
        DocumentHistoryQuery normalized = normalize(Objects.requireNonNull(query, "query must not be null"), true);
        DocumentHistoryQueryRepository.ResultPage resultPage = queryRepository.find(normalized);
        List<DocumentHistoryDTO> items = resultPage.items().stream()
                .map(DocumentHistoryMapper::toDto)
                .collect(Collectors.toList());
        return new DocumentHistoryPageDTO(items, resultPage.totalElements(), normalized.getPage(), normalized.getSize());
    }

    public byte[] exportCsv(DocumentHistoryQuery query) {
        DocumentHistoryQuery normalized = normalize(Objects.requireNonNull(query, "query must not be null"), false)
                .withoutPagination();
        List<DocumentHistory> results = queryRepository.findAll(normalized);
        StringBuilder builder = new StringBuilder();
        builder.append("id;documentType;documentId;action;description;createdAt\n");
        for (DocumentHistory entry : results) {
            builder.append(valueOf(entry.getId()))
                    .append(';')
                    .append(enumValue(entry.getDocumentType()))
                    .append(';')
                    .append(valueOf(entry.getDocumentId()))
                    .append(';')
                    .append(enumValue(entry.getAction()))
                    .append(';')
                    .append(escape(entry.getDescription()))
                    .append(';')
                    .append(entry.getCreatedAt() != null ? CSV_DATE_FORMATTER.format(entry.getCreatedAt()) : "")
                    .append('\n');
        }
        return builder.toString().getBytes(UTF_8);
    }

    private DocumentHistoryQuery normalize(DocumentHistoryQuery query, boolean enforcePagination) {
        DocumentHistoryQuery.Builder builder = DocumentHistoryQuery.builder()
                .documentType(query.getDocumentType())
                .documentId(query.getDocumentId())
                .actions(query.getActions())
                .from(query.getFrom())
                .to(query.getTo())
                .searchText(query.getSearchText());
        if (enforcePagination) {
            int size = query.getSize() > 0 ? Math.min(query.getSize(), MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
            builder.page(query.getPage()).size(size);
        } else {
            builder.page(0).size(0);
        }
        return builder.build();
    }

    private String valueOf(Object value) {
        return value != null ? value.toString() : "";
    }

    private String enumValue(Enum<?> value) {
        return value != null ? value.name() : "";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }
}
