package com.example.server.controller;

import com.example.common.api.ArticleApiContract;
import com.example.common.dto.ArticleDTO;
import com.example.server.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApiContract {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    @GetMapping
    public List<ArticleDTO> listArticles() {
        return articleService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<ArticleDTO> findById(@PathVariable Long id) {
        return articleService.findById(id);
    }

    @Override
    @PostMapping
    public ArticleDTO create(@RequestBody ArticleDTO article) {
        return articleService.create(article);
    }

    @Override
    @PutMapping("/{id}")
    public ArticleDTO update(@PathVariable Long id, @RequestBody ArticleDTO article) {
        return articleService.update(id, article)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = articleService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato");
        }
    }
}
