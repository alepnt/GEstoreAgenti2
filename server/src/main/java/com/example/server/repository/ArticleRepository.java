package com.example.server.repository;

import com.example.server.domain.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {

    List<Article> findAllByOrderByNameAsc();

    Optional<Article> findByCodeIgnoreCase(String code);
}
