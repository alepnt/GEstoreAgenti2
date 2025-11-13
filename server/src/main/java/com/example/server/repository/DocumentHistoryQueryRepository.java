package com.example.server.repository;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import com.example.server.service.DocumentHistoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Repository custom per interrogare lo storico documentale con filtri dinamici e paginazione.
 */
@Repository
public class DocumentHistoryQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<DocumentHistory> ROW_MAPPER = new RowMapper<>() {
        @Override
        public DocumentHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String documentType = rs.getString("document_type");
            Long documentId = rs.getObject("document_id") != null ? rs.getLong("document_id") : null;
            String action = rs.getString("action");
            String description = rs.getString("description");
            Instant createdAt = rs.getTimestamp("created_at").toInstant();
            return new DocumentHistory(
                    id,
                    documentType != null ? DocumentType.valueOf(documentType) : null,
                    documentId,
                    action != null ? DocumentAction.valueOf(action) : null,
                    description,
                    createdAt
            );
        }
    };

    public DocumentHistoryQueryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResultPage find(DocumentHistoryQuery query) {
        QueryParts parts = buildQuery(query);
        MapSqlParameterSource parameters = parts.parameters;
        StringBuilder sql = new StringBuilder("SELECT id, document_type, document_id, action, description, created_at ")
                .append(parts.fromClause)
                .append(" ORDER BY created_at DESC");
        if (query.isPaginated()) {
            sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");
            parameters.addValue("offset", query.offset());
            parameters.addValue("limit", query.getSize());
        }
        List<DocumentHistory> items = jdbcTemplate.query(sql.toString(), parameters, ROW_MAPPER);
        long total = query.isPaginated()
                ? jdbcTemplate.queryForObject("SELECT COUNT(*) " + parts.fromClause, parameters, Long.class)
                : items.size();
        return new ResultPage(items, total);
    }

    public List<DocumentHistory> findAll(DocumentHistoryQuery query) {
        QueryParts parts = buildQuery(query.withoutPagination());
        String sql = "SELECT id, document_type, document_id, action, description, created_at "
                + parts.fromClause
                + " ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, parts.parameters, ROW_MAPPER);
    }

    private QueryParts buildQuery(DocumentHistoryQuery query) {
        StringBuilder fromClause = new StringBuilder("FROM document_history WHERE 1 = 1");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (query.getDocumentType() != null) {
            fromClause.append(" AND document_type = :documentType");
            parameters.addValue("documentType", query.getDocumentType().name());
        }
        if (query.getDocumentId() != null) {
            fromClause.append(" AND document_id = :documentId");
            parameters.addValue("documentId", query.getDocumentId());
        }
        if (!query.getActions().isEmpty()) {
            fromClause.append(" AND action IN (:actions)");
            parameters.addValue("actions", query.getActions().stream().map(Enum::name).toList());
        }
        if (query.getFrom() != null) {
            fromClause.append(" AND created_at >= :from");
            parameters.addValue("from", query.getFrom());
        }
        if (query.getTo() != null) {
            fromClause.append(" AND created_at <= :to");
            parameters.addValue("to", query.getTo());
        }
        if (StringUtils.hasText(query.getSearchText())) {
            fromClause.append(" AND LOWER(description) LIKE :search");
            parameters.addValue("search", "%" + query.getSearchText().toLowerCase() + "%");
        }
        return new QueryParts(fromClause.toString(), parameters);
    }

    public record ResultPage(List<DocumentHistory> items, long totalElements) {
        public ResultPage(List<DocumentHistory> items, long totalElements) {
            this.items = items != null ? items : Collections.emptyList();
            this.totalElements = totalElements;
        }
    }

    private record QueryParts(String fromClause, MapSqlParameterSource parameters) {
    }
}
