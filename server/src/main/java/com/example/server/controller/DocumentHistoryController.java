package com.example.server.controller;

import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.service.DocumentHistoryQuery;
import com.example.server.service.DocumentHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class DocumentHistoryController {

    private final DocumentHistoryService documentHistoryService;

    public DocumentHistoryController(DocumentHistoryService documentHistoryService) {
        this.documentHistoryService = documentHistoryService;
    }

    @GetMapping
    public DocumentHistoryPageDTO search(@RequestParam(value = "documentType", required = false) DocumentType documentType,
                                         @RequestParam(value = "documentId", required = false) Long documentId,
                                         @RequestParam(value = "actions", required = false) List<DocumentAction> actions,
                                         @RequestParam(value = "from", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                         @RequestParam(value = "to", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                         @RequestParam(value = "q", required = false) String search,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "25") int size) {
        DocumentHistoryQuery query = buildQuery(documentType, documentId, actions, from, to, search, page, size);
        return documentHistoryService.search(query);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(value = "documentType", required = false) DocumentType documentType,
                                         @RequestParam(value = "documentId", required = false) Long documentId,
                                         @RequestParam(value = "actions", required = false) List<DocumentAction> actions,
                                         @RequestParam(value = "from", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                         @RequestParam(value = "to", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                         @RequestParam(value = "q", required = false) String search) {
        DocumentHistoryQuery query = buildQuery(documentType, documentId, actions, from, to, search, 0, 0);
        byte[] csv = documentHistoryService.exportCsv(query);
        String filename = "document-history-" + System.currentTimeMillis() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv);
    }

    private DocumentHistoryQuery buildQuery(DocumentType documentType,
                                            Long documentId,
                                            List<DocumentAction> actions,
                                            OffsetDateTime from,
                                            OffsetDateTime to,
                                            String search,
                                            int page,
                                            int size) {
        return DocumentHistoryQuery.builder()
                .documentType(documentType)
                .documentId(documentId)
                .actions(actions != null ? actions : Collections.emptyList())
                .from(from != null ? from.toInstant() : null)
                .to(to != null ? to.toInstant() : null)
                .searchText(StringUtils.hasText(search) ? search : null)
                .page(page)
                .size(size)
                .build();
    }
}
