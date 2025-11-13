package com.example.server.service.mapper;

import com.example.common.dto.ArticleDTO;
import com.example.server.domain.Article;

public final class ArticleMapper {

    private ArticleMapper() {
    }

    public static ArticleDTO toDto(Article article) {
        if (article == null) {
            return null;
        }
        return new ArticleDTO(
                article.getId(),
                article.getCode(),
                article.getName(),
                article.getDescription(),
                article.getUnitPrice(),
                article.getVatRate(),
                article.getUnitOfMeasure(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }

    public static Article fromDto(ArticleDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Article(
                dto.getId(),
                dto.getCode(),
                dto.getName(),
                dto.getDescription(),
                dto.getUnitPrice(),
                dto.getVatRate(),
                dto.getUnitOfMeasure(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
}
