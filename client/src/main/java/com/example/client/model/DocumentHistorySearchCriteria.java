package com.example.client.model;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Rappresenta i filtri applicabili alla ricerca dello storico documentale lato client.
 */
public class DocumentHistorySearchCriteria {

    private DocumentType documentType;
    private Long documentId;
    private List<DocumentAction> actions = new ArrayList<>();
    private Instant from;
    private Instant to;
    private String searchText;

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public List<DocumentAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void setActions(List<DocumentAction> actions) {
        this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String cacheKey(int page, int size) {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(documentType != null ? documentType.name() : "*");
        joiner.add(documentId != null ? documentId.toString() : "*");
        if (!actions.isEmpty()) {
            joiner.add(actions.stream().map(DocumentAction::name).sorted().reduce((a, b) -> a + "," + b).orElse(""));
        } else {
            joiner.add("*");
        }
        joiner.add(from != null ? from.toString() : "*");
        joiner.add(to != null ? to.toString() : "*");
        joiner.add(searchText != null ? searchText : "*");
        joiner.add(Integer.toString(page));
        joiner.add(Integer.toString(size));
        return joiner.toString();
    }
}
