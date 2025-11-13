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
        return articleRepository.findById(id).map(ArticleMapper::toDto);
    }

    @Transactional
    public ArticleDTO create(ArticleDTO dto) {
        validate(dto);
        Article source = ArticleMapper.fromDto(dto);
        Article saved = articleRepository.save(Article.create(
                normalize(source.getCode()),
                normalize(source.getName()),
                normalize(source.getDescription()),
                normalizePrice(source.getUnitPrice()),
                source.getVatRate(),
                normalize(source.getUnitOfMeasure())
        ));
        return ArticleMapper.toDto(saved);
    }

    @Transactional
    public Optional<ArticleDTO> update(Long id, ArticleDTO dto) {
        validate(dto);
        return articleRepository.findById(id)
                .map(existing -> {
                    Article updated = existing.updateFrom(Article.create(
                            normalize(dto.getCode()),
                            normalize(dto.getName()),
                            normalize(dto.getDescription()),
                            normalizePrice(dto.getUnitPrice()),
                            dto.getVatRate(),
                            normalize(dto.getUnitOfMeasure())
                    ));
                    Article saved = articleRepository.save(updated);
                    return ArticleMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return articleRepository.findById(id)
                .map(existing -> {
                    articleRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Article require(Long id) {
        return articleRepository.findById(id)
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
