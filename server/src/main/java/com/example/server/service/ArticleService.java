package com.example.server.service;

import com.example.common.dto.ArticleDTO;
import com.example.server.domain.Article;
import com.example.server.repository.ArticleRepository;
import com.example.server.service.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleDTO> findAll() {
        return articleRepository.findAllByOrderByNameAsc().stream()
                .map(ArticleMapper::toDto)
                .toList();
    }

    public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(ArticleMapper::toDto);
    }

    @Transactional
    public ArticleDTO create(ArticleDTO dto) {
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null");
        validate(validatedDto);
        Article source = Objects.requireNonNull(ArticleMapper.fromDto(validatedDto),
                "mapped article must not be null");
        Article toSave = Objects.requireNonNull(Article.create(
                normalize(source.getCode()),
                normalize(source.getName()),
                normalize(source.getDescription()),
                normalizePrice(source.getUnitPrice()),
                source.getVatRate(),
                normalize(source.getUnitOfMeasure())
        ), "created article must not be null");
        Article saved = articleRepository.save(toSave);
        return ArticleMapper.toDto(saved);
    }

    @Transactional
    public Optional<ArticleDTO> update(Long id, ArticleDTO dto) {
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null");
        validate(validatedDto);
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    Article updateSource = Objects.requireNonNull(Article.create(
                            normalize(validatedDto.getCode()),
                            normalize(validatedDto.getName()),
                            normalize(validatedDto.getDescription()),
                            normalizePrice(validatedDto.getUnitPrice()),
                            validatedDto.getVatRate(),
                            normalize(validatedDto.getUnitOfMeasure())
                    ), "created article must not be null");
                    Article updated = Objects.requireNonNull(existing.updateFrom(updateSource),
                            "updated article must not be null");
                    Article saved = articleRepository.save(updated);
                    return ArticleMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    articleRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Article require(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato"));
    }

    private void validate(ArticleDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio");
        }
        BigDecimal price = dto.getUnitPrice();
        if (price != null && price.signum() < 0) {
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo");
        }
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        return price != null ? price : BigDecimal.ZERO;
    }
}
