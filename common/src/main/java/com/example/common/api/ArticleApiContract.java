package com.example.common.api;

import com.example.common.dto.ArticleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API per la gestione del catalogo articoli.
 */
public interface ArticleApiContract {

    List<ArticleDTO> listArticles();

    Optional<ArticleDTO> findById(Long id);

    ArticleDTO create(ArticleDTO article);

    ArticleDTO update(Long id, ArticleDTO article);

    void delete(Long id);
}
