package com.example.server.service.mapper;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.server.domain.DocumentHistory;

public final class DocumentHistoryMapper {

    private DocumentHistoryMapper() {
    }

    public static DocumentHistoryDTO toDto(DocumentHistory history) {
        if (history == null) {
            return null;
        }
        return new DocumentHistoryDTO(
                history.getId(),
                history.getDocumentType(),
                history.getDocumentId(),
                history.getAction(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }
}
