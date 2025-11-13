package com.example.server.service;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Oggetto immutabile che rappresenta i criteri di ricerca per lo storico documentale.
 */
public final class DocumentHistoryQuery {

    private final DocumentType documentType;
    private final Long documentId;
    private final List<DocumentAction> actions;
    private final Instant from;
    private final Instant to;
    private final String searchText;
    private final int page;
    private final int size;

    private DocumentHistoryQuery(Builder builder) {
        this.documentType = builder.documentType;
        this.documentId = builder.documentId;
        this.actions = builder.actions;
        this.from = builder.from;
        this.to = builder.to;
        this.searchText = builder.searchText;
        this.page = builder.page;
        this.size = builder.size;
    }

    public static Builder builder() {
        return new Builder();
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public List<DocumentAction> getActions() {
        return actions;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public String getSearchText() {
        return searchText;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean isPaginated() {
        return size > 0;
    }

    public int offset() {
        return Math.max(page, 0) * Math.max(size, 0);
    }

    public DocumentHistoryQuery withoutPagination() {
        return builder()
                .documentType(documentType)
                .documentId(documentId)
                .actions(actions)
                .from(from)
                .to(to)
                .searchText(searchText)
                .page(0)
                .size(0)
                .build();
    }

    public String cacheKey() {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(documentType != null ? documentType.name() : "*");
        joiner.add(documentId != null ? documentId.toString() : "*");
        if (!actions.isEmpty()) {
            List<String> sorted = actions.stream()
                    .map(DocumentAction::name)
                    .sorted()
                    .toList();
            joiner.add(String.join(",", sorted));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentHistoryQuery that)) {
            return false;
        }
        return page == that.page
                && size == that.size
                && documentType == that.documentType
                && Objects.equals(documentId, that.documentId)
                && Objects.equals(actions, that.actions)
                && Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && Objects.equals(searchText, that.searchText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentType, documentId, actions, from, to, searchText, page, size);
    }

    public static final class Builder {
        private DocumentType documentType;
        private Long documentId;
        private List<DocumentAction> actions = new ArrayList<>();
        private Instant from;
        private Instant to;
        private String searchText;
        private int page = 0;
        private int size = 25;

        private Builder() {
        }

        private Builder(DocumentHistoryQuery query) {
            this.documentType = query.documentType;
            this.documentId = query.documentId;
            this.actions = new ArrayList<>(query.actions);
            this.from = query.from;
            this.to = query.to;
            this.searchText = query.searchText;
            this.page = query.page;
            this.size = query.size;
        }

        public Builder documentType(DocumentType documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder documentId(Long documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder actions(List<DocumentAction> actions) {
            this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
            return this;
        }

        public Builder from(Instant from) {
            this.from = from;
            return this;
        }

        public Builder to(Instant to) {
            this.to = to;
            return this;
        }

        public Builder searchText(String searchText) {
            this.searchText = searchText;
            return this;
        }

        public Builder page(int page) {
            this.page = Math.max(page, 0);
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public DocumentHistoryQuery build() {
            if (documentId == null && documentType == null && actions.isEmpty() && searchText == null && from == null && to == null) {
                // allow retrieving everything but make sure pagination is enabled to avoid huge responses
                if (size <= 0) {
                    size = 25;
                }
            }
            actions.sort(Comparator.comparing(Enum::name));
            if (searchText != null && !searchText.isBlank()) {
                searchText = searchText.trim();
            } else {
                searchText = null;
            }
            if (size < 0) {
                size = 0;
            }
            return new DocumentHistoryQuery(this);
        }

        public Builder copyOf(DocumentHistoryQuery query) {
            return new Builder(query);
        }
    }
}
