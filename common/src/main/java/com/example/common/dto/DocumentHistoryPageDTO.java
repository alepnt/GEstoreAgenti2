package com.example.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO che rappresenta una pagina di risultati dello storico documentale.
 */
public class DocumentHistoryPageDTO {

    private List<DocumentHistoryDTO> items = new ArrayList<>();
    private long totalElements;
    private int page;
    private int size;

    public DocumentHistoryPageDTO() {
    }

    public DocumentHistoryPageDTO(List<DocumentHistoryDTO> items, long totalElements, int page, int size) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<DocumentHistoryDTO> getItems() {
        return items;
    }

    public void setItems(List<DocumentHistoryDTO> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalPages() {
        if (size <= 0) {
            return 1;
        }
        long pages = totalElements / size;
        if (totalElements % size != 0) {
            pages += 1;
        }
        return Math.max(pages, 1);
    }

    public boolean hasNext() {
        return page + 1 < getTotalPages();
    }

    public boolean hasPrevious() {
        return page > 0 && getTotalPages() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentHistoryPageDTO that)) {
            return false;
        }
        return page == that.page
                && size == that.size
                && totalElements == that.totalElements
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, totalElements, page, size);
    }
}
