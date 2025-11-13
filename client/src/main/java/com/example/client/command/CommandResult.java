package com.example.client.command;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.enums.DocumentType;

import java.util.Collections;
import java.util.List;

/**
 * Risultato generico di un comando, include l'eventuale storico aggiornato.
 */
public record CommandResult<T>(T value,
                               Long documentId,
                               DocumentType documentType,
                               List<DocumentHistoryDTO> historySnapshot) {

    public static <T> CommandResult<T> withoutHistory(T value) {
        return new CommandResult<>(value, null, null, List.of());
    }

    public static <T> CommandResult<T> withHistory(T value,
                                                   Long documentId,
                                                   DocumentType documentType,
                                                   List<DocumentHistoryDTO> historySnapshot) {
        return new CommandResult<>(value, documentId, documentType,
                historySnapshot == null ? List.of() : List.copyOf(historySnapshot));
    }

    @Override
    public List<DocumentHistoryDTO> historySnapshot() {
        return historySnapshot == null ? List.of() : Collections.unmodifiableList(historySnapshot);
    }
}
